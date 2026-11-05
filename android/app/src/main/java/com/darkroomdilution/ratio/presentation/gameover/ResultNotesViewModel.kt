package com.darkroomdilution.ratio.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import com.darkroomdilution.ratio.domain.usecase.SaveRollUseCase
import com.darkroomdilution.ratio.domain.usecase.SuggestNextTimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultNotesViewModel(
    private val repository: DarkroomRepository,
    settingsRepository: SettingsRepository,
    private val saveRoll: SaveRollUseCase,
    private val suggest: SuggestNextTimeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ResultNotesUiState())
    val state: StateFlow<ResultNotesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(celsius = settings.celsius) }
            }
        }
    }

    fun bind(rollId: String) {
        if (_state.value.rollId == rollId) return
        val entry = repository.roll(rollId) ?: return
        _state.update {
            it.copy(
                rollId = entry.id,
                film = entry.film,
                developer = entry.developer,
                dilution = entry.dilution,
                tempC = entry.tempC,
                baseSeconds = entry.baseSeconds,
                correctedSeconds = entry.correctedSeconds,
                pushPull = entry.pushPull,
                elapsedSeconds = entry.elapsedSeconds,
                density = entry.density,
                contrast = entry.contrast,
                notes = entry.notes,
                saved = entry.saved,
                suggestionPercent = suggest(entry.density, entry.contrast).percent
            )
        }
    }

    fun setDensity(value: Density) {
        _state.update {
            it.copy(density = value, suggestionPercent = suggest(value, it.contrast).percent)
        }
    }

    fun setContrast(value: Contrast) {
        _state.update {
            it.copy(contrast = value, suggestionPercent = suggest(it.density, value).percent)
        }
    }

    fun setNotes(value: String) {
        _state.update { it.copy(notes = value) }
    }

    fun commit() {
        val current = _state.value
        if (current.rollId.isEmpty() || current.saved) return
        val existing = repository.roll(current.rollId) ?: return
        saveRoll(
            RollEntry(
                id = existing.id,
                createdAt = existing.createdAt,
                film = current.film,
                developer = current.developer,
                dilution = current.dilution,
                tempC = current.tempC,
                baseSeconds = current.baseSeconds,
                correctedSeconds = current.correctedSeconds,
                pushPull = current.pushPull,
                elapsedSeconds = current.elapsedSeconds,
                density = current.density,
                contrast = current.contrast,
                notes = current.notes,
                saved = true
            )
        )
        _state.update { it.copy(saved = true) }
    }
}
