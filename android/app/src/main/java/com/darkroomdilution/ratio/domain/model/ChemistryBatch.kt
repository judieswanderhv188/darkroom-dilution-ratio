package com.darkroomdilution.ratio.domain.model

data class ChemistryBatch(
    val id: String,
    val name: String,
    val dilution: String,
    val mixedAt: Long,
    val volumeMl: Int,
    val rollsUsed: Int,
    val rollCapacity: Int,
    val shelfLifeDays: Int
) {
    val usedFraction: Float =
        if (rollCapacity <= 0) 0f else (rollsUsed.toFloat() / rollCapacity).coerceIn(0f, 1f)
}
