package com.darkroomdilution.ratio.presentation.labsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.domain.model.LabSettings
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LabSettingsUiState(
    val settings: LabSettings = LabSettings(),
    val showReset: Boolean = false,
    val leadOptions: List<Int> = listOf(0, 3, 5)
)

class LabSettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val repository: DarkroomRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LabSettingsUiState())
    val state: StateFlow<LabSettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { value ->
                _state.update { it.copy(settings = value) }
            }
        }
    }

    fun setCelsius(value: Boolean) {
        settingsRepository.update { it.copy(celsius = value) }
    }

    fun setRedLight(value: Boolean) {
        settingsRepository.update {
            it.copy(redLightMode = value, planningLightMode = if (value) false else it.planningLightMode)
        }
    }

    fun setPlanningLight(value: Boolean) {
        settingsRepository.update {
            it.copy(planningLightMode = value, redLightMode = if (value) false else it.redLightMode)
        }
    }

    fun setSound(value: Boolean) {
        settingsRepository.update { it.copy(agitationSound = value) }
    }

    fun setHaptics(value: Boolean) {
        settingsRepository.update { it.copy(hapticPrompts = value) }
    }

    fun setKeepAwake(value: Boolean) {
        settingsRepository.update { it.copy(keepScreenOn = value) }
    }

    fun setLead(value: Int) {
        settingsRepository.update { it.copy(agitationLeadSeconds = value) }
    }

    fun requestReset() {
        _state.update { it.copy(showReset = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(showReset = false) }
    }

    fun confirmReset() {
        repository.resetAll()
        settingsRepository.update { LabSettings() }
        _state.update { it.copy(showReset = false) }
    }
}
