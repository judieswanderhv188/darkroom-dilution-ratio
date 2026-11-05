package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun SectionHeader(label: String, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary
        )
        ThinRule(
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp)
        )
    }
}
