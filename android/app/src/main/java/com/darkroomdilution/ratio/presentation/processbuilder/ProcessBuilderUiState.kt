package com.darkroomdilution.ratio.presentation.processbuilder

import com.darkroomdilution.ratio.domain.model.ProcessStage

data class ProcessBuilderUiState(
    val stages: List<ProcessStage> = emptyList(),
    val correctedSeconds: Int = 0
)
