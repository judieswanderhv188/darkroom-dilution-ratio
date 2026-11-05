package com.darkroomdilution.ratio.presentation.gameover

import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density

data class ResultNotesUiState(
    val rollId: String = "",
    val film: String = "",
    val developer: String = "",
    val dilution: String = "",
    val tempC: Double = 20.0,
    val celsius: Boolean = true,
    val baseSeconds: Int = 0,
    val correctedSeconds: Int = 0,
    val pushPull: Int = 0,
    val elapsedSeconds: Int = 0,
    val density: Density = Density.NORMAL,
    val contrast: Contrast = Contrast.NORMAL,
    val notes: String = "",
    val suggestionPercent: Int = 0,
    val saved: Boolean = false
)
