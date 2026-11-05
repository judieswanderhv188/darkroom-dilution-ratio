package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.darkroomdilution.ratio.core.theme.LocalPalette
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        containerColor = palette.surface,
        titleContentColor = palette.textPrimary,
        textContentColor = palette.textSecondary,
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyLarge) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textMuted
                )
            }
        }
    )
}
