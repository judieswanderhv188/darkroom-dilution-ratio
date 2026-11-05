package com.darkroomdilution.ratio.presentation.processbuilder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.SecondaryButton
import com.darkroomdilution.ratio.core.ui.SectionHeader
import com.darkroomdilution.ratio.core.ui.SelectableChip
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.agitationLabel
import com.darkroomdilution.ratio.core.ui.stageName
import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.model.formatMmSs

private val agitationModes = listOf(
    AgitationMode.CONTINUOUS,
    AgitationMode.EVERY_10,
    AgitationMode.EVERY_30,
    AgitationMode.EVERY_60,
    AgitationMode.OFF
)

@Composable
fun ProcessBuilderRoute(onBack: () -> Unit, onContinue: () -> Unit) {
    val processViewModel: ProcessBuilderViewModel = viewModel(factory = ServiceLocator.factory)
    val state by processViewModel.state.collectAsStateWithLifecycle()
    ProcessBuilderScreen(
        state = state,
        onBack = onBack,
        onDuration = { index, delta -> processViewModel.stepDuration(index, delta) },
        onAgitation = { index, mode -> processViewModel.setAgitation(index, mode) },
        onToggle = { index, enabled -> processViewModel.toggle(index, enabled) },
        onMove = { index, delta -> processViewModel.move(index, delta) },
        onPresoak = { processViewModel.addPresoak() },
        onContinue = {
            processViewModel.apply()
            onContinue()
        }
    )
}

@Composable
private fun ProcessBuilderScreen(
    state: ProcessBuilderUiState,
    onBack: () -> Unit,
    onDuration: (Int, Int) -> Unit,
    onAgitation: (Int, AgitationMode) -> Unit,
    onToggle: (Int, Boolean) -> Unit,
    onMove: (Int, Int) -> Unit,
    onPresoak: () -> Unit,
    onContinue: () -> Unit
) {
    ScreenScaffold(
        title = stringResource(R.string.title_process),
        onBack = onBack,
        trailing = state.stages.count { it.enabled }.toString()
    ) {
        SectionHeader(label = stringResource(R.string.section_stages))
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            itemsIndexed(state.stages) { index, stage ->
                StageEditorRow(
                    stage = stage,
                    onDuration = { delta -> onDuration(index, delta) },
                    onAgitation = { mode -> onAgitation(index, mode) },
                    onToggle = { enabled -> onToggle(index, enabled) },
                    onMoveUp = { onMove(index, -1) },
                    onMoveDown = { onMove(index, 1) }
                )
                ThinRule()
            }
        }
        SecondaryButton(
            text = stringResource(R.string.add_presoak),
            onClick = onPresoak
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = stringResource(R.string.start_timer),
            onClick = onContinue,
            enabled = state.stages.any { it.enabled }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StageEditorRow(
    stage: ProcessStage,
    onDuration: (Int) -> Unit,
    onAgitation: (AgitationMode) -> Unit,
    onToggle: (Boolean) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val palette = LocalPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stageName(stage.kind),
                style = MaterialTheme.typography.titleMedium,
                color = if (stage.enabled) palette.textPrimary else palette.textMuted,
                modifier = Modifier.weight(1f)
            )
            SquareControl(onClick = onMoveUp) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.cd_move_up),
                    modifier = Modifier.size(20.dp)
                )
            }
            SquareControl(onClick = onMoveDown) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.cd_move_down),
                    modifier = Modifier.size(20.dp)
                )
            }
            Switch(
                checked = stage.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = palette.textPrimary,
                    checkedTrackColor = palette.primary,
                    uncheckedThumbColor = palette.textMuted,
                    uncheckedTrackColor = palette.secondary
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SquareControl(onClick = { onDuration(-GameConfig.STAGE_STEP_SECONDS) }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = formatMmSs(stage.durationSeconds),
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                color = palette.accent
            )
            SquareControl(onClick = { onDuration(GameConfig.STAGE_STEP_SECONDS) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.section_agitation),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            agitationModes.forEach { mode ->
                SelectableChip(
                    label = agitationLabel(mode),
                    selected = mode == stage.agitation,
                    onClick = { onAgitation(mode) },
                    selectedLabel = stringResource(R.string.state_on),
                    unselectedLabel = stringResource(R.string.state_off),
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SquareControl(onClick: () -> Unit, content: @Composable () -> Unit) {
    val palette = LocalPalette.current
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, palette.border),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = palette.secondary,
            contentColor = palette.textPrimary
        ),
        modifier = Modifier.size(48.dp)
    ) {
        content()
    }
}
