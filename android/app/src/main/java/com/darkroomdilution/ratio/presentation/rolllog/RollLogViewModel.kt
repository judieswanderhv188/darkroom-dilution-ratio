package com.darkroomdilution.ratio.presentation.rolllog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RollLogUiState(
    val rolls: List<RollEntry> = emptyList(),
    val selected: RollEntry? = null,
    val celsius: Boolean = true
)

class RollLogViewModel(private val repository: DarkroomRepository) : ViewModel() {

    private val _state = MutableStateFlow(RollLogUiState())
    val state: StateFlow<RollLogUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.rolls.collect { rolls ->
                _state.update { it.copy(rolls = rolls.filter { entry -> entry.saved }) }
            }
        }
    }

    fun open(entry: RollEntry) {
        _state.update { it.copy(selected = entry) }
    }

    fun dismiss() {
        _state.update { it.copy(selected = null) }
    }

    fun reuse() {
        val entry = _state.value.selected ?: return
        repository.updateRecipe {
            it.copy(
                film = entry.film,
                developer = entry.developer,
                dilutionInput = entry.dilution,
                tempC = entry.tempC,
                baseSeconds = entry.baseSeconds,
                pushPull = entry.pushPull,
                correctedSeconds = entry.correctedSeconds
            )
        }
        _state.update { it.copy(selected = null) }
    }
}
