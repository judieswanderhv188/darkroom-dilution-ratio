package com.darkroomdilution.ratio.domain.model

fun formatMmSs(totalSeconds: Int): String {
    val safe = if (totalSeconds < 0) 0 else totalSeconds
    val minutes = safe / 60
    val seconds = safe % 60
    return minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
}

fun formatMl(value: Double): String = String.format("%.1f", value)

fun formatTemp(celsius: Double, useCelsius: Boolean): String =
    if (useCelsius) String.format("%.1f", celsius) else String.format("%.1f", celsius * 9.0 / 5.0 + 32.0)
