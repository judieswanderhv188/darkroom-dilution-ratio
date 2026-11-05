package com.darkroomdilution.ratio.domain.repository

import com.darkroomdilution.ratio.domain.model.LabSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<LabSettings>
    fun update(transform: (LabSettings) -> LabSettings)
}
