package com.darkroomdilution.ratio.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darkroomdilution.ratio.core.theme.LocalPalette
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AgitationDial(
    progress: Float,
    pulsing: Boolean,
    diameter: Dp = 260.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    val transition = rememberInfiniteTransition(label = "dial")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dialSpin"
    )
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dialPulse"
    )
    Box(modifier = modifier.size(diameter)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 3.dp.toPx()
            val inset = stroke * 2f
            val arcSize = Size(size.width - inset * 2f, size.height - inset * 2f)
            drawArc(
                color = palette.border,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke)
            )
            drawArc(
                color = palette.primary,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke)
            )
            val radius = arcSize.width / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val headAngle = Math.toRadians((-90f + 360f * progress.coerceIn(0f, 1f)).toDouble())
            drawCircle(
                color = palette.accent,
                radius = stroke * 1.6f,
                center = Offset(
                    center.x + radius * cos(headAngle).toFloat(),
                    center.y + radius * sin(headAngle).toFloat()
                )
            )
            rotate(degrees = spin, pivot = center) {
                val innerRadius = radius * 0.72f
                val alpha = if (pulsing) pulse else 0.35f
                drawCircle(
                    color = palette.accent.copy(alpha = alpha * 0.5f),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                for (i in 0 until 12) {
                    val angle = Math.toRadians((i * 30).toDouble())
                    val from = Offset(
                        center.x + (innerRadius - 10f) * cos(angle).toFloat(),
                        center.y + (innerRadius - 10f) * sin(angle).toFloat()
                    )
                    val to = Offset(
                        center.x + innerRadius * cos(angle).toFloat(),
                        center.y + innerRadius * sin(angle).toFloat()
                    )
                    drawLine(
                        color = palette.accentDim.copy(alpha = alpha),
                        start = from,
                        end = to,
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}
