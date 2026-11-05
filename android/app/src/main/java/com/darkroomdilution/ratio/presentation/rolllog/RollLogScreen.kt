package com.darkroomdilution.ratio.presentation.rolllog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.LocalPalette
import com.darkroomdilution.ratio.core.ui.PrimaryButton
import com.darkroomdilution.ratio.core.ui.ScreenScaffold
import com.darkroomdilution.ratio.core.ui.ThinRule
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.model.formatMmSs
import com.darkroomdilution.ratio.domain.model.formatTemp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RollLogRoute(onBack: () -> Unit, onReuse: () -> Unit) {
    val logViewModel: RollLogViewModel = viewModel(factory = ServiceLocator.factory)
    val state by logViewModel.state.collectAsStateWithLifecycle()
    RollLogScreen(
        state = state,
        onBack = onBack,
        onOpen = { logViewModel.open(it) },
        onDismiss = { logViewModel.dismiss() },
        onReuse = {
            logViewModel.reuse()
            onReuse()
        },
        onStart = onReuse
    )
}

@Composable
private fun RollLogScreen(
    state: RollLogUiState,
    onBack: () -> Unit,
    onOpen: (RollEntry) -> Unit,
    onDismiss: () -> Unit,
    onReuse: () -> Unit,
    onStart: () -> Unit
) {
    val palette = LocalPalette.current
    ScreenScaffold(
        title = stringResource(R.string.title_log),
        onBack = onBack,
        trailing = state.rolls.size.toString()
    ) {
        if (state.rolls.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_film_strip),
                    contentScale = ContentScale.Fit,
                    alpha = 0.4f,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.empty_log),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textMuted,
                    textAlign = TextAlign.Center
                )
            }
            PrimaryButton(
                text = stringResource(R.string.start_mixing),
                onClick = onStart
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.rolls) { entry ->
                    RollRow(entry = entry, celsius = state.celsius, onClick = { onOpen(entry) })
                    ThinRule()
                }
            }
        }
    }

    val selected = state.selected
    if (selected != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(12.dp),
            containerColor = palette.surface,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
            title = {
                Text(
                    text = selected.film,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(
                            R.string.log_row_detail,
                            selected.developer,
                            selected.dilution,
                            formatMmSs(selected.correctedSeconds)
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTemp(selected.tempC, state.celsius),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onReuse) {
                    Text(
                        text = stringResource(R.string.reuse_recipe),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.close),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textMuted
                    )
                }
            }
        )
    }
}

@Composable
private fun RollRow(entry: RollEntry, celsius: Boolean, onClick: () -> Unit) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(entry.createdAt),
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = palette.textMuted
            )
            Text(
                text = entry.film,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = stringResource(
                    R.string.log_row_detail,
                    entry.developer,
                    entry.dilution,
                    formatTemp(entry.tempC, celsius)
                ),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(palette.accent, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(palette.primary, CircleShape)
            )
        }
    }
}

private fun formatDate(value: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(value))
