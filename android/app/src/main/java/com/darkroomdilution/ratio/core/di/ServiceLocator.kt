package com.darkroomdilution.ratio.core.di

import android.content.Context
import com.darkroomdilution.ratio.data.local.DarkroomPreferences
import com.darkroomdilution.ratio.data.local.FeedbackPlayer
import com.darkroomdilution.ratio.data.repository.DarkroomRepositoryImpl
import com.darkroomdilution.ratio.data.repository.SettingsRepositoryImpl

object ServiceLocator {

    private var factoryRef: AppViewModelFactory? = null

    val factory: AppViewModelFactory
        get() = requireNotNull(factoryRef) { "ServiceLocator.init was not called" }

    fun init(context: Context) {
        if (factoryRef != null) return
        val preferences = DarkroomPreferences(context)
        factoryRef = AppViewModelFactory(
            darkroom = DarkroomRepositoryImpl(preferences),
            settings = SettingsRepositoryImpl(preferences),
            feedback = FeedbackPlayer()
        )
    }
}
