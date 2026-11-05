package com.darkroomdilution.ratio.domain.model

data class MixResult(
    val totalMl: Int,
    val concentrateMl: Double,
    val waterMl: Double,
    val dilution: Dilution,
    val belowMinimum: Boolean
) {
    val concentrateFraction: Float =
        if (totalMl <= 0) 0f else (concentrateMl / totalMl).toFloat().coerceIn(0f, 1f)
    val waterFraction: Float =
        if (totalMl <= 0) 0f else (waterMl / totalMl).toFloat().coerceIn(0f, 1f)
}
