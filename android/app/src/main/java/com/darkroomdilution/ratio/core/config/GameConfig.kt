package com.darkroomdilution.ratio.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val REFERENCE_TEMP_C = 20.0
    const val MIN_TEMP_C = 14.0
    const val MAX_TEMP_C = 30.0
    const val TEMP_STEP_C = 0.5
    const val MIN_CONCENTRATE_ML = 5.0
    const val DEFAULT_TANK_VOLUME_ML = 500
    const val MIN_TANK_VOLUME_ML = 100
    const val MAX_TANK_VOLUME_ML = 3000
    const val TANK_VOLUME_STEP_ML = 10
    const val MIN_CORRECTED_SECONDS = 60
    const val MAX_CORRECTED_SECONDS = 3600
    const val STAGE_STEP_SECONDS = 5
    const val TICK_MS = 100L
    const val AGITATION_WINDOW_SECONDS = 5
    const val STAGE_FLASH_MS = 2000L
    const val DEFAULT_BASE_SECONDS = 780
}
