package com.darkroomdilution.ratio.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPalette = staticCompositionLocalOf { DarkPalette }

@Composable
fun AppTheme(
    lightMode: Boolean = false,
    redLight: Boolean = true,
    content: @Composable () -> Unit
) {
    val palette = when {
        lightMode -> LightPalette
        redLight -> RedLightPalette
        else -> DarkPalette
    }
    val scheme = if (lightMode) {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = LightPalette.surface,
            secondary = palette.accent,
            onSecondary = LightPalette.textPrimary,
            background = palette.bgBase,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            error = palette.primary
        )
    } else {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = palette.textPrimary,
            secondary = palette.accent,
            onSecondary = palette.bgDeep,
            background = palette.bgBase,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            error = palette.primary
        )
    }
    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, content = content)
    }
}
