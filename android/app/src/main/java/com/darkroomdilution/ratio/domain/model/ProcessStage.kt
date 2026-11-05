package com.darkroomdilution.ratio.domain.model

enum class StageKind { PRESOAK, DEVELOPER, STOP, FIX, WASH }

enum class AgitationMode(val intervalSeconds: Int) {
    CONTINUOUS(0),
    EVERY_10(10),
    EVERY_30(30),
    EVERY_60(60),
    OFF(-1)
}

data class ProcessStage(
    val kind: StageKind,
    val durationSeconds: Int,
    val agitation: AgitationMode,
    val enabled: Boolean
)
