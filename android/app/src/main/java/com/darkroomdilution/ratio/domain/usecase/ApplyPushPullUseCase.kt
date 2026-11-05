package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.core.config.GameConfig
import kotlin.math.roundToInt

class ApplyPushPullUseCase {

    fun factor(stops: Int): Double = when (stops) {
        -2 -> 0.70
        -1 -> 0.85
        1 -> 1.40
        2 -> 1.85
        3 -> 2.40
        else -> 1.00
    }

    operator fun invoke(seconds: Int, stops: Int): Int {
        val raw = seconds * factor(stops)
        val rounded = (raw / 5.0).roundToInt() * 5
        return rounded.coerceIn(GameConfig.MIN_CORRECTED_SECONDS, GameConfig.MAX_CORRECTED_SECONDS)
    }

    fun effectiveIso(iso: Int, stops: Int): Int {
        var value = iso.toDouble()
        repeat(kotlin.math.abs(stops)) { value = if (stops > 0) value * 2 else value / 2 }
        return value.roundToInt()
    }
}
