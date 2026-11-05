package com.darkroomdilution.ratio.presentation.timenotebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.data.sample.SampleData
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimeNotebookUiState(
    val entries: List<NotebookEntry> = emptyList(),
    val selectedId: String = "",
    val showEditor: Boolean = false,
    val draftFilm: String = "",
    val draftDeveloper: String = "",
    val draftDilution: String = "1+1",
    val draftIso: Int = 400,
    val draftSeconds: Int = 600,
    val films: List<String> = SampleData.films,
    val developers: List<String> = SampleData.developers,
    val dilutions: List<String> = listOf("STOCK", "1+1", "1+4", "1+14", "1+31", "1+50"),
    val pendingDeleteId: String = ""
) {
    val draftValid: Boolean = draftFilm.isNotBlank() && draftDeveloper.isNotBlank()
}

class TimeNotebookViewModel(private val repository: DarkroomRepository) : ViewModel() {

    private val _state = MutableStateFlow(TimeNotebookUiState())
    val state: StateFlow<TimeNotebookUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.notebook.collect { entries ->
                _state.update { it.copy(entries = entries) }
            }
        }
        viewModelScope.launch {
            repository.recipe.collect { recipe ->
                _state.update { it.copy(selectedId = recipe.selectedNotebookId) }
            }
        }
    }

    fun select(id: String) {
        repository.selectNotebookEntry(id)
    }

    fun openEditor() {
        _state.update {
            it.copy(
                showEditor = true,
                draftFilm = SampleData.films.first(),
                draftDeveloper = SampleData.developers.first(),
                draftDilution = "1+1",
                draftIso = 400,
                draftSeconds = 600
            )
        }
    }

    fun closeEditor() {
        _state.update { it.copy(showEditor = false) }
    }

    fun setFilm(value: String) {
        _state.update { it.copy(draftFilm = value) }
    }

    fun setDeveloper(value: String) {
        _state.update { it.copy(draftDeveloper = value) }
    }

    fun setDilution(value: String) {
        _state.update { it.copy(draftDilution = value) }
    }

    fun stepIso(factor: Int) {
        _state.update {
            val next = if (factor > 0) it.draftIso * 2 else it.draftIso / 2
            it.copy(draftIso = next.coerceIn(25, 6400))
        }
    }

    fun stepSeconds(delta: Int) {
        _state.update {
            it.copy(
                draftSeconds = (it.draftSeconds + delta)
                    .coerceIn(GameConfig.MIN_CORRECTED_SECONDS, GameConfig.MAX_CORRECTED_SECONDS)
            )
        }
    }

    fun confirmEditor() {
        val current = _state.value
        if (!current.draftValid) return
        repository.addNotebookEntry(
            NotebookEntry(
                id = "nb_" + System.currentTimeMillis(),
                film = current.draftFilm,
                developer = current.draftDeveloper,
                dilution = current.draftDilution,
                iso = current.draftIso,
                baseSeconds = current.draftSeconds
            )
        )
        _state.update { it.copy(showEditor = false) }
    }

    fun requestDelete(id: String) {
        _state.update { it.copy(pendingDeleteId = id) }
    }

    fun dismissDelete() {
        _state.update { it.copy(pendingDeleteId = "") }
    }

    fun confirmDelete() {
        val id = _state.value.pendingDeleteId
        if (id.isNotEmpty()) repository.deleteNotebookEntry(id)
        _state.update { it.copy(pendingDeleteId = "") }
    }
}
