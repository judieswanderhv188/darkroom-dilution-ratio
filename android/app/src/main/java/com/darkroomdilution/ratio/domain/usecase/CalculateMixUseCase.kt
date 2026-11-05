package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.domain.model.Dilution
import com.darkroomdilution.ratio.domain.model.MixResult
import com.darkroomdilution.ratio.domain.model.parseDilution
import kotlin.math.round

class CalculateMixUseCase {

    operator fun invoke(volumeMl: Int, dilutionInput: String): MixResult? {
        val dilution = parseDilution(dilutionInput) ?: return null
        if (volumeMl <= 0) return null
        val raw = when (dilution) {
            is Dilution.Stock -> volumeMl.toDouble()
            is Dilution.Parts -> volumeMl.toDouble() / (1.0 + dilution.parts)
            is Dilution.Ratio -> volumeMl.toDouble() / dilution.total
        }
        val concentrate = round(raw * 10.0) / 10.0
        val water = round((volumeMl - concentrate) * 10.0) / 10.0
        return MixResult(
            totalMl = volumeMl,
            concentrateMl = concentrate,
            waterMl = water,
            dilution = dilution,
            belowMinimum = concentrate < GameConfig.MIN_CONCENTRATE_ML
        )
    }
}
