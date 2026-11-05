package com.darkroomdilution.ratio.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.navigation.Screen
import com.darkroomdilution.ratio.core.theme.AppTheme
import com.darkroomdilution.ratio.presentation.chemistryshelf.ChemistryShelfRoute
import com.darkroomdilution.ratio.presentation.game.StageTimerRoute
import com.darkroomdilution.ratio.presentation.gameover.ResultNotesRoute
import com.darkroomdilution.ratio.presentation.labsettings.LabSettingsRoute
import com.darkroomdilution.ratio.presentation.menu.MenuRoute
import com.darkroomdilution.ratio.presentation.mixcalculator.MixCalculatorRoute
import com.darkroomdilution.ratio.presentation.processbuilder.ProcessBuilderRoute
import com.darkroomdilution.ratio.presentation.pushpull.PushPullRoute
import com.darkroomdilution.ratio.presentation.rolllog.RollLogRoute
import com.darkroomdilution.ratio.presentation.safetytips.SafetyTipsRoute
import com.darkroomdilution.ratio.presentation.splash.SplashRoute
import com.darkroomdilution.ratio.presentation.tankpresets.TankPresetsRoute
import com.darkroomdilution.ratio.presentation.tempcompensation.TempCompensationRoute
import com.darkroomdilution.ratio.presentation.timenotebook.TimeNotebookRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val settings by appViewModel.settings.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Menu && screen !is Screen.Splash) {
        appViewModel.back()
    }

    AppTheme(
        lightMode = settings.planningLightMode,
        redLight = settings.redLightMode
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (initialState is Screen.Splash) {
                    fadeIn(tween(700)) togetherWith fadeOut(tween(400))
                } else {
                    (fadeIn(tween(300)) + slideInVertically { it / 12 }) togetherWith
                        fadeOut(tween(220))
                }
            },
            label = "screenHost"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashRoute(onFinished = { appViewModel.replaceWith(Screen.Menu) })
                is Screen.Menu -> MenuRoute(onOpen = { appViewModel.navigate(it) })
                is Screen.MixCalculator -> MixCalculatorRoute(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.TempCompensation) }
                )
                is Screen.TempCompensation -> TempCompensationRoute(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.PushPull) }
                )
                is Screen.PushPull -> PushPullRoute(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.ProcessBuilder) }
                )
                is Screen.ProcessBuilder -> ProcessBuilderRoute(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.Game) }
                )
                is Screen.Game -> StageTimerRoute(
                    onBack = { appViewModel.back() },
                    onFinished = { appViewModel.replaceWith(Screen.GameOver(it)) }
                )
                is Screen.GameOver -> ResultNotesRoute(
                    rollId = target.rollId,
                    onBack = { appViewModel.back() },
                    onAgain = { appViewModel.replaceWith(Screen.MixCalculator) },
                    onMenu = { appViewModel.toMenu() }
                )
                is Screen.RollLog -> RollLogRoute(
                    onBack = { appViewModel.back() },
                    onReuse = { appViewModel.replaceWith(Screen.MixCalculator) }
                )
                is Screen.TankPresets -> TankPresetsRoute(onBack = { appViewModel.back() })
                is Screen.TimeNotebook -> TimeNotebookRoute(onBack = { appViewModel.back() })
                is Screen.ChemistryShelf -> ChemistryShelfRoute(onBack = { appViewModel.back() })
                is Screen.SafetyTips -> SafetyTipsRoute(onBack = { appViewModel.back() })
                is Screen.LabSettings -> LabSettingsRoute(onBack = { appViewModel.back() })
            }
        }
    }
}
