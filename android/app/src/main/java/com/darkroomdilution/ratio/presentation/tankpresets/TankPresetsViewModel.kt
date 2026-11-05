package com.darkroomdilution.ratio.presentation.tankpresets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.domain.model.TankPreset
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TankPresetsUiState(
    val presets: List<TankPreset> = emptyList(),
    val selectedId: String = "",
    val showEditor: Boolean = false,
    val draftName: String = "",
    val draftReels: Int = 1,
    val draftVolume: Int = 300,
    val pendingDeleteId: String = ""
) {
    val draftValid: Boolean = draftName.isNotBlank()
}

class TankPresetsViewModel(private val repository: DarkroomRepository) : ViewModel() {

    private val _state = MutableStateFlow(TankPresetsUiState())
    val state: StateFlow<TankPresetsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.presets.collect { presets ->
                _state.update { it.copy(presets = presets) }
            }
        }
        viewModelScope.launch {
            repository.recipe.collect { recipe ->
                _state.update { it.copy(selectedId = recipe.selectedPresetId) }
            }
        }
    }

    fun select(id: String) {
        repository.selectPreset(id)
    }

    fun openEditor() {
        _state.update {
            it.copy(showEditor = true, draftName = "", draftReels = 1, draftVolume = 300)
        }
    }

    fun closeEditor() {
        _state.update { it.copy(showEditor = false) }
    }

    fun setDraftName(value: String) {
        _state.update { it.copy(draftName = value) }
    }

    fun stepReels(delta: Int) {
        _state.update { it.copy(draftReels = (it.draftReels + delta).coerceIn(1, 8)) }
    }

    fun stepVolume(delta: Int) {
        _state.update {
            it.copy(
                draftVolume = (it.draftVolume + delta)
                    .coerceIn(GameConfig.MIN_TANK_VOLUME_ML, GameConfig.MAX_TANK_VOLUME_ML)
            )
        }
    }

    fun confirmEditor() {
        val current = _state.value
        if (!current.draftValid) return
        repository.addPreset(
            TankPreset(
                id = "tank_" + System.currentTimeMillis(),
                name = current.draftName.trim(),
                reels = current.draftReels,
                volumeMl = current.draftVolume
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
        if (id.isNotEmpty()) repository.deletePreset(id)
        _state.update { it.copy(pendingDeleteId = "") }
    }
}
