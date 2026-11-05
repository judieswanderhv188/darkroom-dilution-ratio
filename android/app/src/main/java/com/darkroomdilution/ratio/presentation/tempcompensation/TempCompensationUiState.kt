package com.darkroomdilution.ratio.presentation.tempcompensation

data class TempCompensationUiState(
    val tempC: Double = 20.0,
    val celsius: Boolean = true,
    val baseSeconds: Int = 780,
    val factor: Double = 1.0,
    val correctedSeconds: Int = 780,
    val ticks: List<Double> = emptyList()
)
