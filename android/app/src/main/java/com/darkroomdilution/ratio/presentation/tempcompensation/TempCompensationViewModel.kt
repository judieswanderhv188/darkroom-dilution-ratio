package com.darkroomdilution.ratio.presentation.tempcompensation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import com.darkroomdilution.ratio.domain.usecase.ApplyPushPullUseCase
import com.darkroomdilution.ratio.domain.usecase.CompensateTemperatureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

class TempCompensationViewModel(
    private val repository: DarkroomRepository,
    settingsRepository: SettingsRepository,
    private val compensate: CompensateTemperatureUseCase,
    private val pushPull: ApplyPushPullUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TempCompensationUiState(ticks = buildTicks()))
    val state: StateFlow<TempCompensationUiState> = _state.asStateFlow()

    init {
        val recipe = repository.recipe.value
        _state.update {
            recompute(it.copy(tempC = recipe.tempC, baseSeconds = recipe.baseSeconds))
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(celsius = settings.celsius) }
            }
        }
        viewModelScope.launch {
            repository.recipe.collect { recipe ->
                _state.update { current ->
                    if (current.baseSeconds == recipe.baseSeconds) {
                        current
                    } else {
                        recompute(current.copy(baseSeconds = recipe.baseSeconds))
                    }
                }
            }
        }
    }

    fun setTemperature(value: Double) {
        _state.update {
            recompute(
                it.copy(
                    tempC = value.coerceIn(GameConfig.MIN_TEMP_C, GameConfig.MAX_TEMP_C)
                )
            )
        }
    }

    fun stepTemperature(delta: Double) {
        setTemperature(round((_state.value.tempC + delta) * 2.0) / 2.0)
    }

    fun apply() {
        val current = _state.value
        repository.updateRecipe {
            it.copy(
                tempC = current.tempC,
                correctedSeconds = pushPull(current.correctedSeconds, it.pushPull)
            )
        }
    }

    private fun recompute(input: TempCompensationUiState): TempCompensationUiState {
        val correction = compensate(input.baseSeconds, input.tempC)
        return input.copy(
            factor = correction.factor,
            correctedSeconds = correction.correctedSeconds
        )
    }

    private fun buildTicks(): List<Double> {
        val result = mutableListOf<Double>()
        var value = GameConfig.MIN_TEMP_C
        while (value <= GameConfig.MAX_TEMP_C + 0.001) {
            result.add(round(value * 2.0) / 2.0)
            value += GameConfig.TEMP_STEP_C
        }
        return result
    }
}
