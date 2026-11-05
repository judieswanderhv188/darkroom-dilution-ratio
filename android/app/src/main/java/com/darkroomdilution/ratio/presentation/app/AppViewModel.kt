package com.darkroomdilution.ratio.presentation.app

import androidx.lifecycle.ViewModel
import com.darkroomdilution.ratio.core.navigation.Screen
import com.darkroomdilution.ratio.domain.model.LabSettings
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(settingsRepository: SettingsRepository) : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    val settings: StateFlow<LabSettings> = settingsRepository.settings

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != target && current !is Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
    }

    fun replaceWith(target: Screen) {
        _screen.value = target
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }
}
