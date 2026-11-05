package com.darkroomdilution.ratio.presentation.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.theme.timerBrush
import com.darkroomdilution.ratio.core.ui.AgitationDial
import com.darkroomdilution.ratio.core.ui.ConfirmDialog
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.core.ui.stageName
import com.darkroomdilution.ratio.core.ui.stageRailName
import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.formatMmSs
import com.darkroomdilution.ratio.domain.model.formatTemp

@Composable
fun StageTimerRoute(onBack: () -> Unit, onFinished: (String) -> Unit) {
    val timerViewModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by timerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { timerViewModel.arm() }
    LaunchedEffect(Unit) {
        timerViewModel.finishedEvents.collect { rollId -> onFinished(rollId) }
    }
    DisposableEffect(Unit) {
        onDispose { timerViewModel.pause() }
    }

    val view = LocalView.current
    DisposableEffect(state.keepScreenOn, state.phase) {
        view.keepScreenOn = state.keepScreenOn && state.phase == TimerPhase.RUNNING
        onDispose { view.keepScreenOn = false }
    }

    val haptics = LocalHapticFeedback.current
    LaunchedEffect(state.agitationPulse) {
        if (state.agitationPulse > 0 && state.haptics) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    StageTimerScreen(
        state = state,
        onBack = onBack,
        onToggle = { timerViewModel.toggleRun() },
        onSkip = { timerViewModel.skipStage() },
        onAbortRequest = { timerViewModel.requestAbort() },
        onAbortDismiss = { timerViewModel.dismissAbort() },
        onAbortConfirm = {
            timerViewModel.dismissAbort()
            onBack()
        }
    )
}

@Composable
private fun StageTimerScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onToggle: () -> Unit,
    onSkip: () -> Unit,
    onAbortRequest: () -> Unit,
    onAbortDismiss: () -> Unit,
    onAbortConfirm: () -> Unit
) {
    val palette = LocalPalette.current
    val stage = state.stages.getOrNull(state.stageIndex)
    val flashAlpha by animateFloatAsState(
        targetValue = if (state.stageFlash) 0.25f else 0f,
        animationSpec = tween(1000),
        label = "stageFlash"
    )
    val description = if (stage != null) {
        stringResource(
            R.string.countdown_state,
            stageName(stage.kind),
            state.remainingSeconds / 60,
            state.remainingSeconds % 60
        )
    } else {
        stringResource(R.string.title_timer)
    }

    ScreenScaffold(
        title = stringResource(R.string.title_timer),
        onBack = onBack,
        overlay = palette.timerBrush(),
        trailing = stringResource(
            R.string.stage_counter,
            (state.stageIndex + 1).coerceAtMost(state.stages.size.coerceAtLeast(1)),
            state.stages.size.coerceAtLeast(1)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.stages.forEachIndexed { index, item ->
                StageRailPill(
                    label = stageRailName(item.kind),
                    active = index == state.stageIndex,
                    done = index < state.stageIndex,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.accent.copy(alpha = flashAlpha))
            )
            AgitationDial(
                progress = state.stageProgress,
                pulsing = state.agitating
            )
            Text(
                text = formatMmSs(state.remainingSeconds),
                style = MaterialTheme.typography.displayLarge.copy(fontFamily = FontFamily.Monospace),
                color = if (state.phase == TimerPhase.PAUSED) {
                    palette.textPrimary.copy(alpha = 0.6f)
                } else {
                    palette.textPrimary
                },
                modifier = Modifier.semantics { stateDescription = description }
            )
        }
        Text(
            text = agitationCaption(state, stage?.agitation),
            style = if (state.agitating) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.labelSmall
            },
            color = if (state.agitating) palette.accent else palette.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
        ThinRule()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StripCell(
                text = stringResource(
                    R.string.strip_temp,
                    formatTemp(state.tempC, state.celsius),
                    if (state.celsius) {
                        stringResource(R.string.unit_celsius)
                    } else {
                        stringResource(R.string.unit_fahrenheit)
                    }
                )
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(palette.rule)
            )
            StripCell(text = stringResource(R.string.strip_dilution, state.dilution))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(palette.rule)
            )
            StripCell(text = stringResource(R.string.strip_push, state.pushPull))
        }
        Surface(
            color = palette.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ControlButton(
                        label = stringResource(R.string.action_pause),
                        onClick = onToggle,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.cd_pause),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    ControlButton(
                        label = stringResource(R.string.skip_stage),
                        onClick = onSkip,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    ControlButton(
                        label = stringResource(R.string.abort),
                        onClick = onAbortRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                PrimaryButton(
                    text = when (state.phase) {
                        TimerPhase.RUNNING -> stringResource(R.string.action_pause)
                        TimerPhase.PAUSED -> stringResource(R.string.action_resume)
                        else -> stringResource(R.string.action_start)
                    },
                    onClick = onToggle,
                    enabled = state.phase != TimerPhase.FINISHED && state.stages.isNotEmpty()
                )
            }
        }
    }

    if (state.showAbort) {
        ConfirmDialog(
            title = stringResource(R.string.abort_title),
            message = stringResource(R.string.abort_message),
            confirmText = stringResource(R.string.abort),
            dismissText = stringResource(R.string.cancel),
            onConfirm = onAbortConfirm,
            onDismiss = onAbortDismiss
        )
    }
}

@Composable
private fun agitationCaption(state: GameUiState, mode: AgitationMode?): String = when {
    state.agitating -> stringResource(R.string.agitate_now)
    mode == AgitationMode.CONTINUOUS -> stringResource(R.string.agitation_constant)
    mode == null || mode == AgitationMode.OFF -> stringResource(R.string.agitation_not_required)
    state.secondsToAgitation >= 0 -> stringResource(
        R.string.next_agitation,
        formatMmSs(state.secondsToAgitation)
    )
    else -> stringResource(R.string.agitation_not_required)
}

@Composable
private fun StageRailPill(
    label: String,
    active: Boolean,
    done: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    val background = when {
        active -> palette.primary
        done -> palette.secondary
        else -> palette.surface
    }
    val textColor = when {
        active -> palette.textPrimary
        done -> palette.accent
        else -> palette.textMuted
    }
    Surface(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(6.dp),
        color = background,
        border = BorderStroke(1.dp, if (active) palette.primary else palette.border)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun StripCell(text: String) {
    val palette = LocalPalette.current
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
        color = palette.textSecondary
    )
}

@Composable
private fun ControlButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val palette = LocalPalette.current
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, palette.border),
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = palette.secondary,
            contentColor = palette.textSecondary
        ),
        modifier = modifier.height(56.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            content()
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
