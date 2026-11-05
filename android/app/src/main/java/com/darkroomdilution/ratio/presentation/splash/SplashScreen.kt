package com.darkroomdilution.ratio.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.theme.splashBrush
import com.darkroomdilution.ratio.core.ui.AppBackdrop
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    var handled by remember { mutableStateOf(false) }
    LaunchedEffect(finished) {
        if (finished && !handled) {
            handled = true
            onFinished()
        }
    }
    SplashScreen()
}

@Composable
private fun SplashScreen() {
    val palette = LocalPalette.current
    val transition = rememberInfiniteTransition(label = "splash")
    val breath by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )
    val ringSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }
    val reveal by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "reveal"
    )

    AppBackdrop(
        overlay = palette.splashBrush(),
        imageAlpha = 0.55f,
        glowAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val stroke = 1.5.dp.toPx()
                    val radius = size.minDimension / 2f - stroke
                    val center = Offset(size.width / 2f, size.height / 2f)
                    rotate(degrees = ringSpin, pivot = center) {
                        drawCircle(
                            color = palette.primary.copy(alpha = 0.28f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = stroke)
                        )
                        for (i in 0 until 12) {
                            val angle = Math.toRadians((i * 30).toDouble())
                            drawLine(
                                color = palette.accentDim.copy(alpha = 0.55f),
                                start = Offset(
                                    center.x + (radius - 14f) * cos(angle).toFloat(),
                                    center.y + (radius - 14f) * sin(angle).toFloat()
                                ),
                                end = Offset(
                                    center.x + radius * cos(angle).toFloat(),
                                    center.y + radius * sin(angle).toFloat()
                                ),
                                strokeWidth = 2f
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer {
                            alpha = breath
                            scaleX = 0.97f + breath * 0.06f
                            scaleY = 0.97f + breath * 0.06f
                        }
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 3.sp),
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = reveal
                    translationY = (1f - reveal) * 48f
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.4.sp),
                color = palette.accentDim,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = reveal }
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                color = palette.primary,
                trackColor = palette.secondary,
                modifier = Modifier
                    .width(200.dp)
                    .height(3.dp)
            )
        }
    }
}
