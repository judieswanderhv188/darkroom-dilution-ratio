package com.darkroomdilution.ratio.presentation.pushpull

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.SelectableChip
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.domain.model.formatMmSs

@Composable
fun PushPullRoute(onBack: () -> Unit, onContinue: () -> Unit) {
    val pushViewModel: PushPullViewModel = viewModel(factory = ServiceLocator.factory)
    val state by pushViewModel.state.collectAsStateWithLifecycle()
    PushPullScreen(
        state = state,
        onBack = onBack,
        onSelect = { pushViewModel.select(it) },
        onContinue = {
            pushViewModel.apply()
            onContinue()
        }
    )
}

@Composable
private fun PushPullScreen(
    state: PushPullUiState,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val palette = LocalPalette.current
    val animatedSeconds by animateIntAsState(
        targetValue = state.resultSeconds,
        animationSpec = tween(300),
        label = "pushResult"
    )
    ScreenScaffold(
        title = stringResource(R.string.title_push),
        onBack = onBack
    ) {
        SectionHeader(label = stringResource(R.string.section_stops))
        Spacer(modifier = Modifier.height(14.dp))
        state.options.chunked(3).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                chunk.forEach { option ->
                    SelectableChip(
                        label = formatStops(option),
                        selected = option == state.stops,
                        onClick = { onSelect(option) },
                        selectedLabel = stringResource(R.string.state_on),
                        unselectedLabel = stringResource(R.string.state_off),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(22.dp))
        ThinRule()
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = formatMmSs(animatedSeconds),
            style = MaterialTheme.typography.displaySmall.copy(fontFamily = FontFamily.Monospace),
            color = palette.accent,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.label_exposure_index, state.iso, state.effectiveIso),
            style = MaterialTheme.typography.titleMedium,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = when {
                state.stops > 0 -> stringResource(R.string.caption_push, state.stops)
                state.stops < 0 -> stringResource(R.string.caption_pull, -state.stops)
                else -> stringResource(R.string.caption_box_speed)
            },
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(18.dp))
        ThinRule()
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(R.string.build_process),
            onClick = onContinue
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun formatStops(stops: Int): String = if (stops > 0) "+" + stops else stops.toString()
