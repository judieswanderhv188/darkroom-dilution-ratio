package com.darkroomdilution.ratio.presentation.mixcalculator

data class MixCalculatorUiState(
    val volumeMl: Int = 500,
    val dilutionInput: String = "1+31",
    val dilutionLabel: String = "1+31",
    val concentrateMl: Double = 0.0,
    val waterMl: Double = 0.0,
    val concentrateFraction: Float = 0f,
    val waterFraction: Float = 0f,
    val valid: Boolean = true,
    val belowMinimum: Boolean = false,
    val savedToShelf: Boolean = false,
    val volumePresets: List<Int> = emptyList(),
    val dilutionPresets: List<String> = emptyList()
)
