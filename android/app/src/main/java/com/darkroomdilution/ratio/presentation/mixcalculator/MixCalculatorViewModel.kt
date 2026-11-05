package com.darkroomdilution.ratio.presentation.mixcalculator

import androidx.lifecycle.ViewModel
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.data.sample.SampleData
import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.usecase.CalculateMixUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MixCalculatorViewModel(
    private val repository: DarkroomRepository,
    private val calculateMix: CalculateMixUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MixCalculatorUiState())
    val state: StateFlow<MixCalculatorUiState> = _state.asStateFlow()

    init {
        val recipe = repository.recipe.value
        _state.value = recompute(
            MixCalculatorUiState(
                volumeMl = recipe.tankVolumeMl,
                dilutionInput = recipe.dilutionInput,
                volumePresets = SampleData.tankVolumePresets,
                dilutionPresets = SampleData.dilutionPresets
            )
        )
    }

    fun setVolume(value: Int) {
        _state.update {
            recompute(
                it.copy(
                    volumeMl = value.coerceIn(
                        GameConfig.MIN_TANK_VOLUME_ML,
                        GameConfig.MAX_TANK_VOLUME_ML
                    ),
                    savedToShelf = false
                )
            )
        }
    }

    fun stepVolume(delta: Int) {
        setVolume(_state.value.volumeMl + delta)
    }

    fun setDilution(value: String) {
        _state.update { recompute(it.copy(dilutionInput = value, savedToShelf = false)) }
    }

    fun apply() {
        val current = _state.value
        if (!current.valid) return
        repository.updateRecipe {
            it.copy(
                tankVolumeMl = current.volumeMl,
                dilutionInput = current.dilutionInput,
                concentrateMl = current.concentrateMl,
                waterMl = current.waterMl
            )
        }
    }

    fun saveToShelf() {
        val current = _state.value
        if (!current.valid) return
        val recipe = repository.recipe.value
        repository.addBatch(
            ChemistryBatch(
                id = "batch_" + System.currentTimeMillis(),
                name = recipe.developer,
                dilution = current.dilutionLabel,
                mixedAt = System.currentTimeMillis(),
                volumeMl = current.volumeMl,
                rollsUsed = 0,
                rollCapacity = 16,
                shelfLifeDays = 90
            )
        )
        _state.update { it.copy(savedToShelf = true) }
    }

    private fun recompute(input: MixCalculatorUiState): MixCalculatorUiState {
        val result = calculateMix(input.volumeMl, input.dilutionInput)
            ?: return input.copy(
                valid = false,
                belowMinimum = false,
                concentrateMl = 0.0,
                waterMl = 0.0,
                concentrateFraction = 0f,
                waterFraction = 0f
            )
        return input.copy(
            valid = true,
            dilutionLabel = result.dilution.label,
            concentrateMl = result.concentrateMl,
            waterMl = result.waterMl,
            concentrateFraction = result.concentrateFraction,
            waterFraction = result.waterFraction,
            belowMinimum = result.belowMinimum
        )
    }
}
