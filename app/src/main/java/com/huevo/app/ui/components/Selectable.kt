package com.huevo.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.huevo.app.ui.theme.CardCorner
import com.huevo.app.ui.theme.ChipCorner
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

/** Fila de opción para preguntas de selección única (radio) o múltiple (check). */
@Composable
fun SelectableOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = false,
    description: String? = null
) {
    val shape = CardCorner
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) PeachSurface else MaterialTheme.colorScheme.surface)
            .then(
                if (!selected) Modifier.outlineBorder(shape) else Modifier
            )
            .let {
                if (multiSelect) {
                    it.toggleable(value = selected, onValueChange = { onClick() })
                } else {
                    it.selectable(selected = selected, onClick = onClick)
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            if (description != null) {
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
        SelectionIndicator(selected = selected, multiSelect = multiSelect)
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean, multiSelect: Boolean) {
    val shape = if (multiSelect) MaterialTheme.shapes.small else CircleShape
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(shape)
            .background(if (selected) OrangePrimary else Color.Transparent)
            .outlineBorder(shape, if (selected) OrangePrimary else PeachSurface, widthOverride = if (selected) 0.dp else 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
        }
    }
}

private fun Modifier.outlineBorder(shape: androidx.compose.ui.graphics.Shape, color: Color = PeachSurface, widthOverride: androidx.compose.ui.unit.Dp = 1.5.dp): Modifier =
    this.border(BorderStroke(widthOverride, color), shape)

/** Chip seleccionable, usado en la parrilla de sentimientos de Impulso. */
@Composable
fun SelectableChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(ChipCorner)
            .background(if (selected) OrangePrimary else PeachSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else OrangePrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}
