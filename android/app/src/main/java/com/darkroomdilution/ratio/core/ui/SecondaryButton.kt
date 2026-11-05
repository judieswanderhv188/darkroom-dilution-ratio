package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: Painter? = null,
    leadingDescription: String? = null
) {
    val palette = LocalPalette.current
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (enabled) palette.border else palette.rule),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = palette.secondary,
            contentColor = palette.textPrimary,
            disabledContainerColor = palette.surface,
            disabledContentColor = palette.textMuted
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leading != null) {
                Icon(
                    painter = leading,
                    contentDescription = leadingDescription,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}
