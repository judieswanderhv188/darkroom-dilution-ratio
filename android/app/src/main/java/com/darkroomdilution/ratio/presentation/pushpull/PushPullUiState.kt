package com.darkroomdilution.ratio.presentation.pushpull

data class PushPullUiState(
    val stops: Int = 0,
    val options: List<Int> = listOf(-2, -1, 0, 1, 2, 3),
    val tempCorrectedSeconds: Int = 780,
    val resultSeconds: Int = 780,
    val iso: Int = 400,
    val effectiveIso: Int = 400
)
