package com.darkroomdilution.ratio.data.repository

import com.darkroomdilution.ratio.data.local.DarkroomPreferences
import com.darkroomdilution.ratio.domain.model.LabSettings
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val preferences: DarkroomPreferences) : SettingsRepository {

    private val state = MutableStateFlow(read())

    override val settings: StateFlow<LabSettings> = state.asStateFlow()

    override fun update(transform: (LabSettings) -> LabSettings) {
        val next = transform(state.value)
        state.value = next
        write(next)
    }

    fun reload() {
        state.value = LabSettings()
        write(state.value)
    }

    private fun read(): LabSettings = LabSettings(
        celsius = preferences.readBoolean(KEY_CELSIUS, true),
        redLightMode = preferences.readBoolean(KEY_RED_LIGHT, true),
        planningLightMode = preferences.readBoolean(KEY_PLANNING_LIGHT, false),
        agitationSound = preferences.readBoolean(KEY_SOUND, true),
        hapticPrompts = preferences.readBoolean(KEY_HAPTIC, true),
        keepScreenOn = preferences.readBoolean(KEY_KEEP_AWAKE, true),
        agitationLeadSeconds = preferences.readInt(KEY_LEAD, 3)
    )

    private fun write(value: LabSettings) {
        preferences.writeBoolean(KEY_CELSIUS, value.celsius)
        preferences.writeBoolean(KEY_RED_LIGHT, value.redLightMode)
        preferences.writeBoolean(KEY_PLANNING_LIGHT, value.planningLightMode)
        preferences.writeBoolean(KEY_SOUND, value.agitationSound)
        preferences.writeBoolean(KEY_HAPTIC, value.hapticPrompts)
        preferences.writeBoolean(KEY_KEEP_AWAKE, value.keepScreenOn)
        preferences.writeInt(KEY_LEAD, value.agitationLeadSeconds)
    }

    private companion object {
        const val KEY_CELSIUS = "settings_celsius"
        const val KEY_RED_LIGHT = "settings_red_light"
        const val KEY_PLANNING_LIGHT = "settings_planning_light"
        const val KEY_SOUND = "settings_sound"
        const val KEY_HAPTIC = "settings_haptic"
        const val KEY_KEEP_AWAKE = "settings_keep_awake"
        const val KEY_LEAD = "settings_lead"
    }
}
