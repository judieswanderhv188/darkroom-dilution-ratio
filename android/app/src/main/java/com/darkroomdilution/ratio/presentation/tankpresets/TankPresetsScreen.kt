package com.darkroomdilution.ratio.presentation.tankpresets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.ConfirmDialog
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ValueStepper
import com.darkroomdilution.ratio.domain.model.TankPreset

@Composable
fun TankPresetsRoute(onBack: () -> Unit) {
    val presetViewModel: TankPresetsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by presetViewModel.state.collectAsStateWithLifecycle()
    TankPresetsScreen(
        state = state,
        onBack = onBack,
        onSelect = { presetViewModel.select(it) },
        onDeleteRequest = { presetViewModel.requestDelete(it) },
        onDeleteDismiss = { presetViewModel.dismissDelete() },
        onDeleteConfirm = { presetViewModel.confirmDelete() },
        onOpenEditor = { presetViewModel.openEditor() },
        onCloseEditor = { presetViewModel.closeEditor() },
        onDraftName = { presetViewModel.setDraftName(it) },
        onReels = { presetViewModel.stepReels(it) },
        onVolume = { presetViewModel.stepVolume(it) },
        onConfirmEditor = { presetViewModel.confirmEditor() }
    )
}

@Composable
private fun TankPresetsScreen(
    state: TankPresetsUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onDeleteRequest: (String) -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onOpenEditor: () -> Unit,
    onCloseEditor: () -> Unit,
    onDraftName: (String) -> Unit,
    onReels: (Int) -> Unit,
    onVolume: (Int) -> Unit,
    onConfirmEditor: () -> Unit
) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_tanks),
        onBack = onBack,
        trailing = state.presets.size.toString()
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = stringResource(R.string.cd_tank),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        SectionHeader(label = stringResource(R.string.title_tanks))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.presets) { preset ->
                PresetRow(
                    preset = preset,
                    selected = preset.id == state.selectedId,
                    onSelect = { onSelect(preset.id) },
                    onDelete = { onDeleteRequest(preset.id) }
                )
                ThinRule()
            }
        }
        PrimaryButton(
            text = stringResource(R.string.add_preset),
            onClick = onOpenEditor
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showEditor) {
        AlertDialog(
            onDismissRequest = onCloseEditor,
            shape = RoundedCornerShape(12.dp),
            containerColor = palette.surface,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
            title = {
                Text(
                    text = stringResource(R.string.add_preset),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.field_name),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    OutlinedTextField(
                        value = state.draftName,
                        onValueChange = onDraftName,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.surfaceAlt,
                            unfocusedContainerColor = palette.surfaceAlt,
                            focusedTextColor = palette.textPrimary,
                            unfocusedTextColor = palette.textPrimary,
                            focusedBorderColor = palette.primary,
                            unfocusedBorderColor = palette.border,
                            cursorColor = palette.accent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.field_reels),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = state.draftReels.toString(),
                        unit = stringResource(R.string.field_reels),
                        onDecrease = { onReels(-1) },
                        onIncrease = { onReels(1) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.field_volume),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = state.draftVolume.toString(),
                        unit = stringResource(R.string.unit_ml),
                        onDecrease = { onVolume(-GameConfig.TANK_VOLUME_STEP_ML) },
                        onIncrease = { onVolume(GameConfig.TANK_VOLUME_STEP_ML) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmEditor, enabled = state.draftValid) {
                    Text(
                        text = stringResource(R.string.save_entry),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.draftValid) palette.primary else palette.textMuted
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseEditor) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textMuted
                    )
                }
            }
        )
    }

    if (state.pendingDeleteId.isNotEmpty()) {
        ConfirmDialog(
            title = stringResource(R.string.delete_title),
            message = stringResource(R.string.delete_message),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = onDeleteConfirm,
            onDismiss = onDeleteDismiss
        )
    }
}

@Composable
private fun PresetRow(
    preset: TankPreset,
    selected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) palette.accent else palette.textPrimary
            )
            Text(
                text = stringResource(R.string.preset_detail, preset.reels, preset.volumeMl),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = palette.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
