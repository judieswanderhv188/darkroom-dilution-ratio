package com.darkroomdilution.ratio.presentation.labsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.ConfirmDialog
import com.darkroomdilution.ratio.core.ui.SecondaryButton
import com.darkroomdilution.ratio.core.ui.SelectableChip
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ScreenScaffold

@Composable
fun LabSettingsRoute(onBack: () -> Unit) {
    val settingsViewModel: LabSettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    LabSettingsScreen(
        state = state,
        onBack = onBack,
        onCelsius = { settingsViewModel.setCelsius(it) },
        onRedLight = { settingsViewModel.setRedLight(it) },
        onPlanningLight = { settingsViewModel.setPlanningLight(it) },
        onSound = { settingsViewModel.setSound(it) },
        onHaptics = { settingsViewModel.setHaptics(it) },
        onKeepAwake = { settingsViewModel.setKeepAwake(it) },
        onLead = { settingsViewModel.setLead(it) },
        onResetRequest = { settingsViewModel.requestReset() },
        onResetDismiss = { settingsViewModel.dismissReset() },
        onResetConfirm = { settingsViewModel.confirmReset() }
    )
}

@Composable
private fun LabSettingsScreen(
    state: LabSettingsUiState,
    onBack: () -> Unit,
    onCelsius: (Boolean) -> Unit,
    onRedLight: (Boolean) -> Unit,
    onPlanningLight: (Boolean) -> Unit,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onKeepAwake: (Boolean) -> Unit,
    onLead: (Int) -> Unit,
    onResetRequest: () -> Unit,
    onResetDismiss: () -> Unit,
    onResetConfirm: () -> Unit
) {
    val palette = LocalPalette.current
    val settings = state.settings
    ScreenScaffold(
        title = stringResource(R.string.title_settings),
        onBack = onBack
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text(
                        text = stringResource(R.string.setting_unit),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SelectableChip(
                            label = stringResource(R.string.unit_celsius_long),
                            selected = settings.celsius,
                            onClick = { onCelsius(true) },
                            selectedLabel = stringResource(R.string.state_on),
                            unselectedLabel = stringResource(R.string.state_off),
                            modifier = Modifier.weight(1f),
                            compact = true
                        )
                        SelectableChip(
                            label = stringResource(R.string.unit_fahrenheit_long),
                            selected = !settings.celsius,
                            onClick = { onCelsius(false) },
                            selectedLabel = stringResource(R.string.state_on),
                            unselectedLabel = stringResource(R.string.state_off),
                            modifier = Modifier.weight(1f),
                            compact = true
                        )
                    }
                }
                ThinRule()
            }
            item {
                SettingSwitchRow(
                    title = stringResource(R.string.setting_red_light),
                    hint = stringResource(R.string.setting_red_light_hint),
                    checked = settings.redLightMode,
                    onChange = onRedLight
                )
                ThinRule()
            }
            item {
                SettingSwitchRow(
                    title = stringResource(R.string.setting_planning_light),
                    hint = stringResource(R.string.setting_planning_light_hint),
                    checked = settings.planningLightMode,
                    onChange = onPlanningLight
                )
                ThinRule()
            }
            item {
                SettingSwitchRow(
                    title = stringResource(R.string.setting_sound),
                    hint = null,
                    checked = settings.agitationSound,
                    onChange = onSound
                )
                ThinRule()
            }
            item {
                SettingSwitchRow(
                    title = stringResource(R.string.setting_haptic),
                    hint = null,
                    checked = settings.hapticPrompts,
                    onChange = onHaptics
                )
                ThinRule()
            }
            item {
                SettingSwitchRow(
                    title = stringResource(R.string.setting_keep_awake),
                    hint = null,
                    checked = settings.keepScreenOn,
                    onChange = onKeepAwake
                )
                ThinRule()
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text(
                        text = stringResource(R.string.setting_lead),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.leadOptions.forEach { option ->
                            SelectableChip(
                                label = leadLabel(option),
                                selected = option == settings.agitationLeadSeconds,
                                onClick = { onLead(option) },
                                selectedLabel = stringResource(R.string.state_on),
                                unselectedLabel = stringResource(R.string.state_off),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                ThinRule()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        SecondaryButton(
            text = stringResource(R.string.reset_all_data),
            onClick = onResetRequest
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showReset) {
        ConfirmDialog(
            title = stringResource(R.string.reset_title),
            message = stringResource(R.string.reset_message),
            confirmText = stringResource(R.string.reset_all_data),
            dismissText = stringResource(R.string.cancel),
            onConfirm = onResetConfirm,
            onDismiss = onResetDismiss
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    hint: String?,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val palette = LocalPalette.current
    val stateLabel = if (checked) {
        stringResource(R.string.state_on)
    } else {
        stringResource(R.string.state_off)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.textPrimary,
                checkedTrackColor = palette.primary,
                uncheckedThumbColor = palette.textMuted,
                uncheckedTrackColor = palette.secondary
            ),
            modifier = Modifier.semantics { stateDescription = stateLabel }
        )
    }
}

@Composable
private fun leadLabel(value: Int): String = when (value) {
    0 -> stringResource(R.string.lead_0)
    3 -> stringResource(R.string.lead_3)
    else -> stringResource(R.string.lead_5)
}
