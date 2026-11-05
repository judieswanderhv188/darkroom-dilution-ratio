package com.darkroomdilution.ratio.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.theme.resultBrush
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SecondaryButton
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.SelectableChip
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density
import com.darkroomdilution.ratio.domain.model.formatMmSs
import com.darkroomdilution.ratio.domain.model.formatTemp

@Composable
fun ResultNotesRoute(
    rollId: String,
    onBack: () -> Unit,
    onAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: ResultNotesViewModel = viewModel(factory = ServiceLocator.factory)
    val state by resultViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(rollId) { resultViewModel.bind(rollId) }
    ResultNotesScreen(
        state = state,
        onBack = onBack,
        onDensity = { resultViewModel.setDensity(it) },
        onContrast = { resultViewModel.setContrast(it) },
        onNotes = { resultViewModel.setNotes(it) },
        onAgain = {
            resultViewModel.commit()
            onAgain()
        },
        onMenu = onMenu
    )
}

@Composable
private fun ResultNotesScreen(
    state: ResultNotesUiState,
    onBack: () -> Unit,
    onDensity: (Density) -> Unit,
    onContrast: (Contrast) -> Unit,
    onNotes: (String) -> Unit,
    onAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val palette = LocalPalette.current
    val transition = rememberInfiniteTransition(label = "result")
    val tilt by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )
    ScreenScaffold(
        title = stringResource(R.string.title_result),
        onBack = onBack,
        overlay = palette.resultBrush()
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_film_strip),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(96.dp)
                        .graphicsLayer { rotationZ = tilt }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.label_total_elapsed),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Text(
                    text = formatMmSs(state.elapsedSeconds),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = palette.accent
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SectionHeader(label = stringResource(R.string.section_recipe))
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = palette.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        RecapRow(stringResource(R.string.row_film), state.film)
                        ThinRule()
                        RecapRow(stringResource(R.string.row_developer), state.developer)
                        ThinRule()
                        RecapRow(stringResource(R.string.row_dilution), state.dilution)
                        ThinRule()
                        RecapRow(
                            stringResource(R.string.row_temp),
                            formatTemp(state.tempC, state.celsius)
                        )
                        ThinRule()
                        RecapRow(
                            stringResource(R.string.row_base),
                            formatMmSs(state.baseSeconds)
                        )
                        ThinRule()
                        RecapRow(
                            stringResource(R.string.row_corrected),
                            formatMmSs(state.correctedSeconds)
                        )
                        ThinRule()
                        RecapRow(
                            stringResource(R.string.row_push),
                            formatStops(state.pushPull)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
            item {
                SectionHeader(label = stringResource(R.string.section_density))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    densityOptions.forEach { option ->
                        SelectableChip(
                            label = densityLabel(option),
                            selected = option == state.density,
                            onClick = { onDensity(option) },
                            selectedLabel = stringResource(R.string.state_on),
                            unselectedLabel = stringResource(R.string.state_off),
                            modifier = Modifier.weight(1f),
                            compact = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(label = stringResource(R.string.section_contrast))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    contrastOptions.forEach { option ->
                        SelectableChip(
                            label = contrastLabel(option),
                            selected = option == state.contrast,
                            onClick = { onContrast(option) },
                            selectedLabel = stringResource(R.string.state_on),
                            unselectedLabel = stringResource(R.string.state_off),
                            modifier = Modifier.weight(1f),
                            compact = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    text = suggestionText(state),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.accent
                )
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(label = stringResource(R.string.section_notes))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.hint_notes),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotes,
                    minLines = 3,
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.surface,
                        unfocusedContainerColor = palette.surface,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = palette.border,
                        cursorColor = palette.accent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
        PrimaryButton(
            text = stringResource(R.string.save_and_develop_again),
            onClick = onAgain
        )
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(
            text = stringResource(R.string.menu),
            onClick = onMenu,
            leading = painterResource(R.drawable.icon_home),
            leadingDescription = stringResource(R.string.cd_home)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private val densityOptions = listOf(
    Density.THIN,
    Density.LIGHT,
    Density.NORMAL,
    Density.FULL,
    Density.DENSE
)

private val contrastOptions = listOf(
    Contrast.FLAT,
    Contrast.SOFT,
    Contrast.NORMAL,
    Contrast.PUNCHY,
    Contrast.HARD
)

@Composable
private fun densityLabel(value: Density): String = when (value) {
    Density.THIN -> stringResource(R.string.density_thin)
    Density.LIGHT -> stringResource(R.string.density_light)
    Density.NORMAL -> stringResource(R.string.density_normal)
    Density.FULL -> stringResource(R.string.density_full)
    Density.DENSE -> stringResource(R.string.density_dense)
}

@Composable
private fun contrastLabel(value: Contrast): String = when (value) {
    Contrast.FLAT -> stringResource(R.string.contrast_flat)
    Contrast.SOFT -> stringResource(R.string.contrast_soft)
    Contrast.NORMAL -> stringResource(R.string.contrast_normal)
    Contrast.PUNCHY -> stringResource(R.string.contrast_punchy)
    Contrast.HARD -> stringResource(R.string.contrast_hard)
}

@Composable
private fun suggestionText(state: ResultNotesUiState): String {
    val label = densityLabel(state.density)
    return when {
        state.suggestionPercent > 0 ->
            stringResource(R.string.suggest_longer, label, state.suggestionPercent)
        state.suggestionPercent < 0 ->
            stringResource(R.string.suggest_shorter, label, -state.suggestionPercent)
        else -> stringResource(R.string.suggest_keep, label)
    }
}

@Composable
private fun RecapRow(label: String, value: String) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
            color = palette.textPrimary,
            textAlign = TextAlign.End
        )
    }
}

private fun formatStops(stops: Int): String = if (stops > 0) "+" + stops else stops.toString()
