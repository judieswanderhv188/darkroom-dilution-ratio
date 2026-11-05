package com.darkroomdilution.ratio.presentation.chemistryshelf

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.ChipRow
import com.darkroomdilution.ratio.core.ui.ConfirmDialog
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ValueStepper
import com.darkroomdilution.ratio.domain.model.ChemistryBatch

@Composable
fun ChemistryShelfRoute(onBack: () -> Unit) {
    val shelfViewModel: ChemistryShelfViewModel = viewModel(factory = ServiceLocator.factory)
    val state by shelfViewModel.state.collectAsStateWithLifecycle()
    ChemistryShelfScreen(
        state = state,
        onBack = onBack,
        onConsume = { shelfViewModel.consume(it) },
        onDeleteRequest = { shelfViewModel.requestDelete(it) },
        onDeleteDismiss = { shelfViewModel.dismissDelete() },
        onDeleteConfirm = { shelfViewModel.confirmDelete() },
        onOpenEditor = { shelfViewModel.openEditor() },
        onCloseEditor = { shelfViewModel.closeEditor() },
        onName = { shelfViewModel.setName(it) },
        onVolume = { shelfViewModel.stepVolume(it) },
        onCapacity = { shelfViewModel.stepCapacity(it) },
        onShelfLife = { shelfViewModel.stepShelfLife(it) },
        onConfirmEditor = { shelfViewModel.confirmEditor() }
    )
}

@Composable
private fun ChemistryShelfScreen(
    state: ChemistryShelfUiState,
    onBack: () -> Unit,
    onConsume: (String) -> Unit,
    onDeleteRequest: (String) -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onOpenEditor: () -> Unit,
    onCloseEditor: () -> Unit,
    onName: (Int) -> Unit,
    onVolume: (Int) -> Unit,
    onCapacity: (Int) -> Unit,
    onShelfLife: (Int) -> Unit,
    onConfirmEditor: () -> Unit
) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_shelf),
        onBack = onBack,
        trailing = state.batches.size.toString()
    ) {
        if (state.batches.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_shelf),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.batches) { batch ->
                    BatchRow(
                        batch = batch,
                        now = state.now,
                        onConsume = { onConsume(batch.id) },
                        onDelete = { onDeleteRequest(batch.id) }
                    )
                    ThinRule()
                }
            }
        }
        PrimaryButton(
            text = stringResource(R.string.mix_new_batch),
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
                    text = stringResource(R.string.mix_new_batch),
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
                    ChipRow(
                        options = state.names,
                        selectedIndex = state.draftIndex,
                        onSelect = onName,
                        selectedLabel = stringResource(R.string.state_on),
                        unselectedLabel = stringResource(R.string.state_off)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_capacity),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = state.draftCapacity.toString(),
                        unit = stringResource(R.string.field_capacity),
                        onDecrease = { onCapacity(-1) },
                        onIncrease = { onCapacity(1) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_shelf_life),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = state.draftShelfLife.toString(),
                        unit = stringResource(R.string.field_shelf_life),
                        onDecrease = { onShelfLife(-7) },
                        onIncrease = { onShelfLife(7) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmEditor) {
                    Text(
                        text = stringResource(R.string.save_entry),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primary
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
private fun BatchRow(
    batch: ChemistryBatch,
    now: Long,
    onConsume: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = LocalPalette.current
    val ageDays = ((now - batch.mixedAt) / 86_400_000L).toInt()
    val expired = ageDays > batch.shelfLifeDays
    val barColor = if (batch.usedFraction > 0.75f) palette.primary else palette.accent
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = stringResource(R.string.cd_film_strip),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = batch.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                Text(
                    text = stringResource(R.string.batch_age, batch.dilution, ageDays),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
            }
            IconButton(onClick = onConsume, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.plus_one_roll),
                    tint = palette.accent,
                    modifier = Modifier.size(20.dp)
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.batch_capacity, batch.rollsUsed, batch.rollCapacity),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
            if (expired) {
                Text(
                    text = stringResource(R.string.batch_replace),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(palette.secondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(batch.usedFraction.coerceIn(0.001f, 1f))
                    .height(4.dp)
                    .background(barColor)
            )
        }
    }
}
