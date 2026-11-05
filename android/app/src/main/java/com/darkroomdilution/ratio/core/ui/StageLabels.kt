package com.darkroomdilution.ratio.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.StageKind

@Composable
fun stageName(kind: StageKind): String = when (kind) {
    StageKind.PRESOAK -> stringResource(R.string.stage_presoak)
    StageKind.DEVELOPER -> stringResource(R.string.stage_developer)
    StageKind.STOP -> stringResource(R.string.stage_stop)
    StageKind.FIX -> stringResource(R.string.stage_fix)
    StageKind.WASH -> stringResource(R.string.stage_wash)
}

@Composable
fun stageRailName(kind: StageKind): String = when (kind) {
    StageKind.PRESOAK -> stringResource(R.string.rail_presoak)
    StageKind.DEVELOPER -> stringResource(R.string.rail_developer)
    StageKind.STOP -> stringResource(R.string.rail_stop)
    StageKind.FIX -> stringResource(R.string.rail_fix)
    StageKind.WASH -> stringResource(R.string.rail_wash)
}

@Composable
fun agitationLabel(mode: AgitationMode): String = when (mode) {
    AgitationMode.CONTINUOUS -> stringResource(R.string.agitation_continuous)
    AgitationMode.EVERY_10 -> stringResource(R.string.agitation_10)
    AgitationMode.EVERY_30 -> stringResource(R.string.agitation_30)
    AgitationMode.EVERY_60 -> stringResource(R.string.agitation_60)
    AgitationMode.OFF -> stringResource(R.string.agitation_off)
}
