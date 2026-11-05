package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density

data class TimeSuggestion(val percent: Int, val density: Density, val contrast: Contrast)

class SuggestNextTimeUseCase {

    operator fun invoke(density: Density, contrast: Contrast): TimeSuggestion {
        val fromDensity = when (density) {
            Density.THIN -> 12
            Density.LIGHT -> 6
            Density.NORMAL -> 0
            Density.FULL -> -6
            Density.DENSE -> -12
        }
        val fromContrast = when (contrast) {
            Contrast.FLAT -> 5
            Contrast.SOFT -> 2
            Contrast.NORMAL -> 0
            Contrast.PUNCHY -> -2
            Contrast.HARD -> -5
        }
        return TimeSuggestion(
            percent = (fromDensity + fromContrast).coerceIn(-25, 25),
            density = density,
            contrast = contrast
        )
    }
}
