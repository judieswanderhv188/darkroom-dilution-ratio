package com.darkroomdilution.ratio.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class Palette(
    val bgBase: Color,
    val bgDeep: Color,
    val secondary: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val primary: Color,
    val primaryDim: Color,
    val accent: Color,
    val accentDim: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val rule: Color,
    val border: Color
)

val DarkPalette = Palette(
    bgBase = Color(0xFF120B0B),
    bgDeep = Color(0xFF0C0707),
    secondary = Color(0xFF2E1A1A),
    surface = Color(0xFF1C1010),
    surfaceAlt = Color(0xFF241414),
    primary = Color(0xFFC8352F),
    primaryDim = Color(0xFF8E221E),
    accent = Color(0xFFF0A04B),
    accentDim = Color(0xFFA86A2F),
    textPrimary = Color(0xFFF2DEDC),
    textSecondary = Color(0xFFB89490),
    textMuted = Color(0xFF7E605D),
    rule = Color(0xFF4A2422),
    border = Color(0xFF3A2020)
)

val RedLightPalette = DarkPalette.copy(
    accent = Color(0xFFC2803A),
    accentDim = Color(0xFF8A5726),
    textPrimary = Color(0xFFE6CECB)
)

val LightPalette = Palette(
    bgBase = Color(0xFFF4EFEC),
    bgDeep = Color(0xFFE6DCD7),
    secondary = Color(0xFFEDE3DF),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFF7F1EE),
    primary = Color(0xFFB02A25),
    primaryDim = Color(0xFF8E221E),
    accent = Color(0xFF9A5C16),
    accentDim = Color(0xFFBE8140),
    textPrimary = Color(0xFF1A1010),
    textSecondary = Color(0xFF5B4542),
    textMuted = Color(0xFF8E7873),
    rule = Color(0xFFD9C8C4),
    border = Color(0xFFCBB9B5)
)

fun Palette.backdropBrush(): Brush = Brush.verticalGradient(listOf(bgDeep, bgBase, surface))

fun Palette.splashBrush(): Brush = Brush.verticalGradient(listOf(bgDeep, bgBase, bgDeep))

fun Palette.resultBrush(): Brush = Brush.verticalGradient(listOf(surfaceAlt, bgBase, bgDeep))

fun Palette.timerBrush(): Brush = Brush.verticalGradient(listOf(bgBase, bgDeep, bgBase))

fun Palette.primaryButtonBrush(): Brush = Brush.horizontalGradient(listOf(primary, primaryDim))

fun Palette.accentButtonBrush(): Brush = Brush.horizontalGradient(listOf(accent, accentDim))

fun Palette.glowBrush(alpha: Float = 0.18f): Brush =
    Brush.radialGradient(colors = listOf(primary.copy(alpha = alpha), Color.Transparent), radius = 520f)
