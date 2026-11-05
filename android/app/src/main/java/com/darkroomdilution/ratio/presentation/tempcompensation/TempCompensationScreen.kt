package com.darkroomdilution.ratio.presentation.tempcompensation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.SelectableChip
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ValueStepper
import com.darkroomdilution.ratio.domain.model.formatMmSs
import com.darkroomdilution.ratio.domain.model.formatTemp

@Composable
fun TempCompensationRoute(onBack: () -> Unit, onContinue: () -> Unit) {
    val tempViewModel: TempCompensationViewModel = viewModel(factory = ServiceLocator.factory)
    val state by tempViewModel.state.collectAsStateWithLifecycle()
    TempCompensationScreen(
        state = state,
        onBack = onBack,
        onSelect = { tempViewModel.setTemperature(it) },
        onStep = { tempViewModel.stepTemperature(it) },
        onContinue = {
            tempViewModel.apply()
            onContinue()
        }
    )
}

@Composable
private fun TempCompensationScreen(
    state: TempCompensationUiState,
    onBack: () -> Unit,
    onSelect: (Double) -> Unit,
    onStep: (Double) -> Unit,
    onContinue: () -> Unit
) {
    val palette = LocalPalette.current
    val unit = if (state.celsius) {
        stringResource(R.string.unit_celsius)
    } else {
        stringResource(R.string.unit_fahrenheit)
    }
    ScreenScaffold(
        title = stringResource(R.string.title_temp),
        onBack = onBack,
        trailing = unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = stringResource(R.string.cd_thermometer),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(96.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_corrected_time),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Text(
                    text = formatMmSs(state.correctedSeconds),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = palette.accent
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        ThinRule()
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(label = stringResource(R.string.section_temperature))
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(state.ticks) { _, tick ->
                SelectableChip(
                    label = formatTemp(tick, state.celsius),
                    selected = kotlin.math.abs(tick - state.tempC) < 0.01,
                    onClick = { onSelect(tick) },
                    selectedLabel = stringResource(R.string.state_on),
                    unselectedLabel = stringResource(R.string.state_off)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ValueStepper(
            value = formatTemp(state.tempC, state.celsius),
            unit = unit,
            onDecrease = { onStep(-GameConfig.TEMP_STEP_C) },
            onIncrease = { onStep(GameConfig.TEMP_STEP_C) }
        )
        Spacer(modifier = Modifier.height(18.dp))
        ThinRule()
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            ReadoutCell(
                label = stringResource(R.string.label_base_time),
                value = formatMmSs(state.baseSeconds),
                modifier = Modifier.weight(1f)
            )
            ReadoutCell(
                label = stringResource(R.string.label_factor),
                value = formatFactor(state.factor),
                modifier = Modifier.weight(1f)
            )
            ReadoutCell(
                label = stringResource(R.string.label_corrected_time),
                value = formatMmSs(state.correctedSeconds),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(R.string.apply_and_continue),
            onClick = onContinue
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReadoutCell(label: String, value: String, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
            color = palette.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatFactor(factor: Double): String =
    "x" + (kotlin.math.round(factor * 100.0) / 100.0).toString()
