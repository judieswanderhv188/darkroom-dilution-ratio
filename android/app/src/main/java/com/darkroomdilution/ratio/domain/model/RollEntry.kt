package com.darkroomdilution.ratio.domain.model

enum class Density { THIN, LIGHT, NORMAL, FULL, DENSE }

enum class Contrast { FLAT, SOFT, NORMAL, PUNCHY, HARD }

data class RollEntry(
    val id: String,
    val createdAt: Long,
    val film: String,
    val developer: String,
    val dilution: String,
    val tempC: Double,
    val baseSeconds: Int,
    val correctedSeconds: Int,
    val pushPull: Int,
    val elapsedSeconds: Int,
    val density: Density,
    val contrast: Contrast,
    val notes: String,
    val saved: Boolean
)
