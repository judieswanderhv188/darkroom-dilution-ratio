package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkroomdilution.ratio.R
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun ModuleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.accentDim,
            modifier = Modifier.size(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = palette.textMuted,
                maxLines = 2
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.cd_forward),
            tint = palette.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
