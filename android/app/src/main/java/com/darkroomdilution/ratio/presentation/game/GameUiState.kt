package com.darkroomdilution.ratio.presentation.game

import com.darkroomdilution.ratio.domain.model.ProcessStage

enum class TimerPhase { IDLE, RUNNING, PAUSED, FINISHED }

data class GameUiState(
    val stages: List<ProcessStage> = emptyList(),
    val stageIndex: Int = 0,
    val stageDuration: Int = 0,
    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val phase: TimerPhase = TimerPhase.IDLE,
    val agitating: Boolean = false,
    val agitationPulse: Int = 0,
    val secondsToAgitation: Int = -1,
    val stageFlash: Boolean = false,
    val showAbort: Boolean = false,
    val tempC: Double = 20.0,
    val celsius: Boolean = true,
    val dilution: String = "",
    val pushPull: Int = 0,
    val keepScreenOn: Boolean = true,
    val haptics: Boolean = true
) {
    val stageProgress: Float =
        if (stageDuration <= 0) 0f else remainingSeconds.toFloat() / stageDuration.toFloat()
}
