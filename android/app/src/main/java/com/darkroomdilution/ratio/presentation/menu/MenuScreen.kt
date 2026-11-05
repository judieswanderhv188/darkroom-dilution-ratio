package com.darkroomdilution.ratio.presentation.menu

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.navigation.Screen
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.theme.backdropBrush
import com.darkroomdilution.ratio.core.ui.AppBackdrop
import com.darkroomdilution.ratio.core.ui.ModuleRow
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.StatCard
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.domain.model.formatMmSs

private data class MenuModule(
    val titleRes: Int,
    val hintRes: Int,
    val icon: ImageVector,
    val target: Screen
)

private val menuModules = listOf(
    MenuModule(R.string.module_mix, R.string.module_mix_hint, Icons.Default.Build, Screen.MixCalculator),
    MenuModule(R.string.module_timer, R.string.module_timer_hint, Icons.Default.PlayArrow, Screen.Game),
    MenuModule(R.string.module_temp, R.string.module_temp_hint, Icons.Default.Info, Screen.TempCompensation),
    MenuModule(R.string.module_push, R.string.module_push_hint, Icons.Default.KeyboardArrowUp, Screen.PushPull),
    MenuModule(R.string.module_notebook, R.string.module_notebook_hint, Icons.Default.DateRange, Screen.TimeNotebook),
    MenuModule(R.string.module_process, R.string.module_process_hint, Icons.Default.Create, Screen.ProcessBuilder),
    MenuModule(R.string.module_tanks, R.string.module_tanks_hint, Icons.Default.AddCircle, Screen.TankPresets),
    MenuModule(R.string.module_log, R.string.module_log_hint, Icons.Default.Menu, Screen.RollLog),
    MenuModule(R.string.module_shelf, R.string.module_shelf_hint, Icons.Default.ShoppingCart, Screen.ChemistryShelf),
    MenuModule(R.string.module_safety, R.string.module_safety_hint, Icons.Default.Warning, Screen.SafetyTips),
    MenuModule(R.string.module_settings, R.string.module_settings_hint, Icons.Default.Settings, Screen.LabSettings)
)

@Composable
fun MenuRoute(onOpen: (Screen) -> Unit) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    MenuScreen(state = state, onOpen = onOpen)
}

@Composable
private fun MenuScreen(state: MenuUiState, onOpen: (Screen) -> Unit) {
    val palette = LocalPalette.current
    val animatedRolls by animateIntAsState(
        targetValue = state.rollCount,
        animationSpec = tween(500),
        label = "rolls"
    )
    val animatedBatches by animateIntAsState(
        targetValue = state.openBatches,
        animationSpec = tween(500),
        label = "batches"
    )
    AppBackdrop(
        overlay = palette.backdropBrush(),
        imageAlpha = 0.35f,
        glowAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.masthead_top),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            letterSpacing = 1.sp
                        ),
                        maxLines = 1,
                        color = palette.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.masthead_bottom),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            letterSpacing = 1.sp
                        ),
                        maxLines = 1,
                        color = palette.primary
                    )
                }
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = stringResource(R.string.cd_tank),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ThinRule()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
            MenuStatStrip(
                rolls = animatedRolls,
                lastDevSeconds = state.lastDevSeconds,
                batches = animatedBatches
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(R.string.start_mixing),
                onClick = { onOpen(Screen.MixCalculator) },
                icon = Icons.Default.PlayArrow
            )
            Spacer(modifier = Modifier.height(18.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(menuModules) { module ->
                    ModuleRow(
                        icon = module.icon,
                        title = stringResource(module.titleRes),
                        subtitle = stringResource(module.hintRes),
                        onClick = { onOpen(module.target) }
                    )
                    ThinRule()
                }
            }
        }
    }
}

@Composable
private fun MenuStatStrip(rolls: Int, lastDevSeconds: Int, batches: Int) {
    val palette = LocalPalette.current
    val visible = mutableListOf<Triple<String, String, androidx.compose.ui.graphics.Color>>()
    if (rolls > 0) {
        visible.add(Triple(stringResource(R.string.stat_rolls), rolls.toString(), palette.accent))
    }
    if (lastDevSeconds > 0) {
        visible.add(
            Triple(
                stringResource(R.string.stat_last_dev),
                formatMmSs(lastDevSeconds),
                palette.primary
            )
        )
    }
    if (batches > 0) {
        visible.add(Triple(stringResource(R.string.stat_shelf), batches.toString(), palette.accentDim))
    }
    if (visible.size < 2) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        visible.forEach { entry ->
            StatCard(
                label = entry.first,
                value = entry.second,
                accent = entry.third,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
