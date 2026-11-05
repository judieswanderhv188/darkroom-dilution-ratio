package com.darkroomdilution.ratio.data.local

import android.media.AudioManager
import android.media.ToneGenerator

class FeedbackPlayer {

    private var generator: ToneGenerator? = null

    fun beep(durationMs: Int) {
        runCatching {
            val active = generator ?: ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70).also {
                generator = it
            }
            active.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs)
        }
    }

    fun release() {
        runCatching { generator?.release() }
        generator = null
    }
}
