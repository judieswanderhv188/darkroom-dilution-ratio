package com.darkroomdilution.ratio.presentation.safetytips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.domain.model.SafetyTopic

@Composable
fun SafetyTipsRoute(onBack: () -> Unit) {
    val safetyViewModel: SafetyTipsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by safetyViewModel.state.collectAsStateWithLifecycle()
    SafetyTipsScreen(state = state, onBack = onBack)
}

@Composable
private fun SafetyTipsScreen(state: SafetyTipsUiState, onBack: () -> Unit) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_safety),
        onBack = onBack,
        trailing = state.topics.size.toString()
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.topics) { topic ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)) {
                    Text(
                        text = headingOf(topic),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.accent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bodyOf(topic),
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textSecondary
                    )
                }
                ThinRule()
            }
        }
    }
}

@Composable
private fun headingOf(topic: SafetyTopic): String = when (topic) {
    SafetyTopic.VENTILATION -> stringResource(R.string.safety_ventilation)
    SafetyTopic.GLOVES -> stringResource(R.string.safety_gloves)
    SafetyTopic.STOP_FUMES -> stringResource(R.string.safety_stop)
    SafetyTopic.FIXER_DISPOSAL -> stringResource(R.string.safety_fixer)
    SafetyTopic.SILVER_RECOVERY -> stringResource(R.string.safety_silver)
    SafetyTopic.STORAGE -> stringResource(R.string.safety_storage)
    SafetyTopic.SPILL_RESPONSE -> stringResource(R.string.safety_spill)
    SafetyTopic.FOOD_SEPARATION -> stringResource(R.string.safety_food)
}

@Composable
private fun bodyOf(topic: SafetyTopic): String = when (topic) {
    SafetyTopic.VENTILATION -> stringResource(R.string.safety_ventilation_body)
    SafetyTopic.GLOVES -> stringResource(R.string.safety_gloves_body)
    SafetyTopic.STOP_FUMES -> stringResource(R.string.safety_stop_body)
    SafetyTopic.FIXER_DISPOSAL -> stringResource(R.string.safety_fixer_body)
    SafetyTopic.SILVER_RECOVERY -> stringResource(R.string.safety_silver_body)
    SafetyTopic.STORAGE -> stringResource(R.string.safety_storage_body)
    SafetyTopic.SPILL_RESPONSE -> stringResource(R.string.safety_spill_body)
    SafetyTopic.FOOD_SEPARATION -> stringResource(R.string.safety_food_body)
}
