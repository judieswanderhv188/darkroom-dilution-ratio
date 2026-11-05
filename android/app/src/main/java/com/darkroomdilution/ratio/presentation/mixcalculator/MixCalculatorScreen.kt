package com.darkroomdilution.ratio.presentation.mixcalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.ChipRow
import com.darkroomdilution.ratio.core.ui.GraduatedCylinder
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SecondaryButton
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ValueStepper
import com.darkroomdilution.ratio.domain.model.formatMl

@Composable
fun MixCalculatorRoute(onBack: () -> Unit, onContinue: () -> Unit) {
    val mixViewModel: MixCalculatorViewModel = viewModel(factory = ServiceLocator.factory)
    val state by mixViewModel.state.collectAsStateWithLifecycle()
    MixCalculatorScreen(
        state = state,
        onBack = onBack,
        onVolumeStep = { mixViewModel.stepVolume(it) },
        onVolumePreset = { mixViewModel.setVolume(it) },
        onDilutionChange = { mixViewModel.setDilution(it) },
        onShelf = { mixViewModel.saveToShelf() },
        onContinue = {
            mixViewModel.apply()
            onContinue()
        }
    )
}

@Composable
private fun MixCalculatorScreen(
    state: MixCalculatorUiState,
    onBack: () -> Unit,
    onVolumeStep: (Int) -> Unit,
    onVolumePreset: (Int) -> Unit,
    onDilutionChange: (String) -> Unit,
    onShelf: () -> Unit,
    onContinue: () -> Unit
) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_mix),
        onBack = onBack,
        trailing = stringResource(R.string.unit_ml)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                SectionHeader(label = stringResource(R.string.section_tank_volume))
                Spacer(modifier = Modifier.height(10.dp))
                ChipRow(
                    options = state.volumePresets.map { it.toString() },
                    selectedIndex = state.volumePresets.indexOf(state.volumeMl),
                    onSelect = { index -> onVolumePreset(state.volumePresets[index]) },
                    selectedLabel = stringResource(R.string.state_on),
                    unselectedLabel = stringResource(R.string.state_off)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ValueStepper(
                    value = state.volumeMl.toString(),
                    unit = stringResource(R.string.unit_ml),
                    onDecrease = { onVolumeStep(-GameConfig.TANK_VOLUME_STEP_ML) },
                    onIncrease = { onVolumeStep(GameConfig.TANK_VOLUME_STEP_ML) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ThinRule()
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SectionHeader(label = stringResource(R.string.section_dilution))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.hint_dilution),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.dilutionInput,
                    onValueChange = onDilutionChange,
                    singleLine = true,
                    isError = !state.valid,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.surface,
                        unfocusedContainerColor = palette.surface,
                        errorContainerColor = palette.surface,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = palette.border,
                        cursorColor = palette.accent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                ChipRow(
                    options = state.dilutionPresets,
                    selectedIndex = state.dilutionPresets.indexOf(state.dilutionInput),
                    onSelect = { index -> onDilutionChange(state.dilutionPresets[index]) },
                    selectedLabel = stringResource(R.string.state_on),
                    unselectedLabel = stringResource(R.string.state_off)
                )
                if (!state.valid) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.error_dilution),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.primary
                    )
                }
                if (state.valid && state.belowMinimum) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.warn_below_minimum),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.primary
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    GraduatedCylinder(
                        spriteRes = R.drawable.sprite_1,
                        spriteDescription = stringResource(R.string.cd_cylinder_concentrate),
                        fillFraction = state.concentrateFraction,
                        value = formatMl(state.concentrateMl),
                        unit = stringResource(R.string.label_concentrate),
                        tint = palette.primary
                    )
                    GraduatedCylinder(
                        spriteRes = R.drawable.sprite_2,
                        spriteDescription = stringResource(R.string.cd_cylinder_water),
                        fillFraction = state.waterFraction,
                        value = formatMl(state.waterMl),
                        unit = stringResource(R.string.label_water),
                        tint = palette.accentDim
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ThinRule()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        R.string.summary_mix,
                        state.volumeMl,
                        state.dilutionLabel,
                        formatMl(state.concentrateMl),
                        formatMl(state.waterMl)
                    ),
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        PrimaryButton(
            text = stringResource(R.string.use_this_mix),
            onClick = onContinue,
            enabled = state.valid
        )
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(
            text = stringResource(R.string.save_to_shelf),
            onClick = onShelf,
            enabled = state.valid && !state.savedToShelf
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
