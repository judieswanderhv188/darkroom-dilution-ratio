package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.theme.backdropBrush
import com.darkroomdilution.ratio.core.theme.glowBrush

@Composable
fun AppBackdrop(
    overlay: Brush,
    imageAlpha: Float,
    glowAlignment: Alignment,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = LocalPalette.current
    Box(modifier = modifier.fillMaxSize().background(palette.bgBase)) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = imageAlpha,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(overlay))
        Box(
            modifier = Modifier
                .align(glowAlignment)
                .size(320.dp)
                .background(palette.glowBrush())
        )
        content()
    }
}

@Composable
fun ScreenScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    overlay: Brush? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalPalette.current
    AppBackdrop(
        overlay = overlay ?: palette.backdropBrush(),
        imageAlpha = 0.35f,
        glowAlignment = Alignment.TopEnd,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = palette.textSecondary),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.cd_back),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 1.sp
                    ),
                    color = palette.textPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (trailing != null) {
                        Text(
                            text = trailing,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.accentDim
                        )
                    }
                }
            }
            ThinRule()
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
