package com.darkroomdilution.ratio.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun GraduatedCylinder(
    spriteRes: Int,
    spriteDescription: String,
    fillFraction: Float,
    value: String,
    unit: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    val animated by animateFloatAsState(
        targetValue = fillFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "cylinderFill"
    )
    Column(
        modifier = modifier.width(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(84.dp)
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(spriteRes),
                contentDescription = spriteDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val insetX = size.width * 0.22f
                val innerWidth = size.width - insetX * 2f
                val top = size.height * 0.12f
                val bottom = size.height * 0.92f
                val column = bottom - top
                val fillHeight = column * animated
                drawRect(
                    color = tint.copy(alpha = 0.55f),
                    topLeft = Offset(insetX, bottom - fillHeight),
                    size = Size(innerWidth, fillHeight)
                )
                for (i in 0..10) {
                    val y = bottom - column * (i / 10f)
                    val long = i % 5 == 0
                    drawLine(
                        color = palette.accentDim.copy(alpha = if (long) 0.85f else 0.4f),
                        start = Offset(insetX, y),
                        end = Offset(insetX + innerWidth * (if (long) 0.55f else 0.3f), y),
                        strokeWidth = 1.5f
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.sp
            ),
            color = palette.accent,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
