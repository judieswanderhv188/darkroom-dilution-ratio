package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.model.StageKind

class BuildProcessUseCase {

    operator fun invoke(developerSeconds: Int, existing: List<ProcessStage>): List<ProcessStage> {
        if (existing.isEmpty()) {
            return listOf(
                ProcessStage(StageKind.DEVELOPER, developerSeconds, AgitationMode.EVERY_60, true),
                ProcessStage(StageKind.STOP, 60, AgitationMode.CONTINUOUS, true),
                ProcessStage(StageKind.FIX, 300, AgitationMode.EVERY_60, true),
                ProcessStage(StageKind.WASH, 300, AgitationMode.OFF, true)
            )
        }
        return existing.map { stage ->
            if (stage.kind == StageKind.DEVELOPER) stage.copy(durationSeconds = developerSeconds) else stage
        }
    }
}
