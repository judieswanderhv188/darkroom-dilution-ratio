package com.darkroomdilution.ratio.presentation.timenotebook

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.darkroomdilution.ratio.core.ui.ConfirmDialog
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.ValueStepper
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.model.formatMmSs

@Composable
fun TimeNotebookRoute(onBack: () -> Unit) {
    val notebookViewModel: TimeNotebookViewModel = viewModel(factory = ServiceLocator.factory)
    val state by notebookViewModel.state.collectAsStateWithLifecycle()
    TimeNotebookScreen(
        state = state,
        onBack = onBack,
        onSelect = { notebookViewModel.select(it) },
        onDeleteRequest = { notebookViewModel.requestDelete(it) },
        onDeleteDismiss = { notebookViewModel.dismissDelete() },
        onDeleteConfirm = { notebookViewModel.confirmDelete() },
        onOpenEditor = { notebookViewModel.openEditor() },
        onCloseEditor = { notebookViewModel.closeEditor() },
        onFilm = { notebookViewModel.setFilm(it) },
        onDeveloper = { notebookViewModel.setDeveloper(it) },
        onDilution = { notebookViewModel.setDilution(it) },
        onIso = { notebookViewModel.stepIso(it) },
        onSeconds = { notebookViewModel.stepSeconds(it) },
        onConfirmEditor = { notebookViewModel.confirmEditor() }
    )
}

@Composable
private fun TimeNotebookScreen(
    state: TimeNotebookUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onDeleteRequest: (String) -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onOpenEditor: () -> Unit,
    onCloseEditor: () -> Unit,
    onFilm: (String) -> Unit,
    onDeveloper: (String) -> Unit,
    onDilution: (String) -> Unit,
    onIso: (Int) -> Unit,
    onSeconds: (Int) -> Unit,
    onConfirmEditor: () -> Unit
) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_notebook),
        onBack = onBack,
        trailing = state.entries.size.toString()
    ) {
        SectionHeader(label = stringResource(R.string.field_base_seconds))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.entries) { entry ->
                NotebookRow(
                    entry = entry,
                    selected = entry.id == state.selectedId,
                    onSelect = { onSelect(entry.id) },
                    onDelete = { onDeleteRequest(entry.id) }
                )
                ThinRule()
            }
        }
        PrimaryButton(
            text = stringResource(R.string.add_entry),
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
                    text = stringResource(R.string.add_entry),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.field_film),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ChipRow(
                        options = state.films,
                        selectedIndex = state.films.indexOf(state.draftFilm),
                        onSelect = { index -> onFilm(state.films[index]) },
                        selectedLabel = stringResource(R.string.state_on),
                        unselectedLabel = stringResource(R.string.state_off)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_developer),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ChipRow(
                        options = state.developers,
                        selectedIndex = state.developers.indexOf(state.draftDeveloper),
                        onSelect = { index -> onDeveloper(state.developers[index]) },
                        selectedLabel = stringResource(R.string.state_on),
                        unselectedLabel = stringResource(R.string.state_off)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_dilution),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ChipRow(
                        options = state.dilutions,
                        selectedIndex = state.dilutions.indexOf(state.draftDilution),
                        onSelect = { index -> onDilution(state.dilutions[index]) },
                        selectedLabel = stringResource(R.string.state_on),
                        unselectedLabel = stringResource(R.string.state_off)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_iso),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = state.draftIso.toString(),
                        unit = stringResource(R.string.field_iso),
                        onDecrease = { onIso(-1) },
                        onIncrease = { onIso(1) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.field_base_seconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    ValueStepper(
                        value = formatMmSs(state.draftSeconds),
                        unit = stringResource(R.string.field_base_seconds),
                        onDecrease = { onSeconds(-GameConfig.STAGE_STEP_SECONDS) },
                        onIncrease = { onSeconds(GameConfig.STAGE_STEP_SECONDS) }
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
private fun NotebookRow(
    entry: NotebookEntry,
    selected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.film,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) palette.accent else palette.textPrimary
            )
            Text(
                text = stringResource(
                    R.string.notebook_detail,
                    entry.developer,
                    entry.dilution,
                    entry.iso
                ),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
        Text(
            text = formatMmSs(entry.baseSeconds),
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
            color = palette.accent
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = palette.primary,
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
}
