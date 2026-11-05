package com.darkroomdilution.ratio.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.darkroomdilution.ratio.data.local.FeedbackPlayer
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import com.darkroomdilution.ratio.domain.usecase.ApplyPushPullUseCase
import com.darkroomdilution.ratio.domain.usecase.BuildProcessUseCase
import com.darkroomdilution.ratio.domain.usecase.CalculateMixUseCase
import com.darkroomdilution.ratio.domain.usecase.CompensateTemperatureUseCase
import com.darkroomdilution.ratio.domain.usecase.SaveRollUseCase
import com.darkroomdilution.ratio.domain.usecase.SuggestNextTimeUseCase
import com.darkroomdilution.ratio.presentation.app.AppViewModel
import com.darkroomdilution.ratio.presentation.chemistryshelf.ChemistryShelfViewModel
import com.darkroomdilution.ratio.presentation.game.GameViewModel
import com.darkroomdilution.ratio.presentation.gameover.ResultNotesViewModel
import com.darkroomdilution.ratio.presentation.labsettings.LabSettingsViewModel
import com.darkroomdilution.ratio.presentation.menu.MenuViewModel
import com.darkroomdilution.ratio.presentation.mixcalculator.MixCalculatorViewModel
import com.darkroomdilution.ratio.presentation.processbuilder.ProcessBuilderViewModel
import com.darkroomdilution.ratio.presentation.pushpull.PushPullViewModel
import com.darkroomdilution.ratio.presentation.rolllog.RollLogViewModel
import com.darkroomdilution.ratio.presentation.safetytips.SafetyTipsViewModel
import com.darkroomdilution.ratio.presentation.splash.SplashViewModel
import com.darkroomdilution.ratio.presentation.tankpresets.TankPresetsViewModel
import com.darkroomdilution.ratio.presentation.tempcompensation.TempCompensationViewModel
import com.darkroomdilution.ratio.presentation.timenotebook.TimeNotebookViewModel

class AppViewModelFactory(
    private val darkroom: DarkroomRepository,
    private val settings: SettingsRepository,
    private val feedback: FeedbackPlayer
) : ViewModelProvider.Factory {

    private val calculateMix = CalculateMixUseCase()
    private val compensate = CompensateTemperatureUseCase()
    private val pushPull = ApplyPushPullUseCase()
    private val buildProcess = BuildProcessUseCase()
    private val saveRoll = SaveRollUseCase(darkroom)
    private val suggest = SuggestNextTimeUseCase()

    @Suppress("DEPRECATION", "DEPRECATION_ERROR")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when (modelClass) {
            AppViewModel::class.java -> AppViewModel(settings)
            SplashViewModel::class.java -> SplashViewModel()
            MenuViewModel::class.java -> MenuViewModel(darkroom)
            MixCalculatorViewModel::class.java -> MixCalculatorViewModel(darkroom, calculateMix)
            TempCompensationViewModel::class.java ->
                TempCompensationViewModel(darkroom, settings, compensate, pushPull)
            PushPullViewModel::class.java -> PushPullViewModel(darkroom, compensate, pushPull)
            ProcessBuilderViewModel::class.java -> ProcessBuilderViewModel(darkroom, buildProcess)
            GameViewModel::class.java -> GameViewModel(darkroom, settings, feedback)
            ResultNotesViewModel::class.java -> ResultNotesViewModel(darkroom, settings, saveRoll, suggest)
            RollLogViewModel::class.java -> RollLogViewModel(darkroom)
            TankPresetsViewModel::class.java -> TankPresetsViewModel(darkroom)
            TimeNotebookViewModel::class.java -> TimeNotebookViewModel(darkroom)
            ChemistryShelfViewModel::class.java -> ChemistryShelfViewModel(darkroom)
            SafetyTipsViewModel::class.java -> SafetyTipsViewModel()
            LabSettingsViewModel::class.java -> LabSettingsViewModel(settings, darkroom)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
