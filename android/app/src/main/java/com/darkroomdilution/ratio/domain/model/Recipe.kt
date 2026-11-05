package com.darkroomdilution.ratio.domain.model

import com.darkroomdilution.ratio.core.config.GameConfig

data class Recipe(
    val tankVolumeMl: Int = GameConfig.DEFAULT_TANK_VOLUME_ML,
    val dilutionInput: String = "1+31",
    val concentrateMl: Double = 15.6,
    val waterMl: Double = 484.4,
    val film: String = "HP5 PLUS 400",
    val developer: String = "ID-11",
    val baseSeconds: Int = GameConfig.DEFAULT_BASE_SECONDS,
    val tempC: Double = GameConfig.REFERENCE_TEMP_C,
    val pushPull: Int = 0,
    val correctedSeconds: Int = GameConfig.DEFAULT_BASE_SECONDS,
    val selectedPresetId: String = "",
    val selectedNotebookId: String = "",
    val stages: List<ProcessStage> = emptyList()
)
