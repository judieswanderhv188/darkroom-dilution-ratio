package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun ValueStepper(
    value: String,
    unit: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    decreaseDescription: String? = null,
    increaseDescription: String? = null
) {
    val palette = LocalPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onDecrease,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, palette.border),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = palette.secondary,
                contentColor = palette.textPrimary
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = decreaseDescription,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(fontFamily = FontFamily.Monospace),
                color = palette.accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedButton(
            onClick = onIncrease,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, palette.border),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = palette.secondary,
                contentColor = palette.textPrimary
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = increaseDescription,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
