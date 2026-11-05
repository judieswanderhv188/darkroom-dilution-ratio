package com.darkroomdilution.ratio.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object MixCalculator : Screen
    data object TempCompensation : Screen
    data object PushPull : Screen
    data object ProcessBuilder : Screen
    data object Game : Screen
    data class GameOver(val rollId: String) : Screen
    data object RollLog : Screen
    data object TankPresets : Screen
    data object TimeNotebook : Screen
    data object ChemistryShelf : Screen
    data object SafetyTips : Screen
    data object LabSettings : Screen
}
