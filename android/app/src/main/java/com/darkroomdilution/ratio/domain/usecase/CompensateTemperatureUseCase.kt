package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.core.config.GameConfig
import kotlin.math.pow
import kotlin.math.roundToInt

data class TemperatureCorrection(val factor: Double, val correctedSeconds: Int)

class CompensateTemperatureUseCase {

    operator fun invoke(baseSeconds: Int, tempC: Double): TemperatureCorrection {
        val factor = 2.0.pow((GameConfig.REFERENCE_TEMP_C - tempC) / 10.0)
        val raw = baseSeconds * factor
        val rounded = (raw / 5.0).roundToInt() * 5
        val clamped = rounded.coerceIn(GameConfig.MIN_CORRECTED_SECONDS, GameConfig.MAX_CORRECTED_SECONDS)
        return TemperatureCorrection(factor = factor, correctedSeconds = clamped)
    }
}
