package com.darkroomdilution.ratio.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkroomdilution.ratio.core.theme.LocalPalette

@Composable
fun ChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedLabel: String,
    unselectedLabel: String
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(options) { index, option ->
            SelectableChip(
                label = option,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                selectedLabel = selectedLabel,
                unselectedLabel = unselectedLabel
            )
        }
    }
}

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedLabel: String,
    unselectedLabel: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val palette = LocalPalette.current
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        contentPadding = PaddingValues(horizontal = 6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) palette.surfaceAlt else palette.surface,
            contentColor = if (selected) palette.accent else palette.textSecondary
        ),
        modifier = modifier
            .height(56.dp)
            .defaultMinSize(minWidth = 64.dp)
            .semantics { stateDescription = if (selected) selectedLabel else unselectedLabel }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 2.dp)) {
            Text(
                text = label,
                style = if (compact) {
                    MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp)
                } else {
                    MaterialTheme.typography.titleMedium
                },
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}
