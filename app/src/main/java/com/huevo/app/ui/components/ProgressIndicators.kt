package com.huevo.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun HuevoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: androidx.compose.ui.graphics.Color = PeachSurface,
    fillColor: androidx.compose.ui.graphics.Color = OrangePrimary,
    height: androidx.compose.ui.unit.Dp = 10.dp
) {
    val animated by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(500), label = "progress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        )
    }
}

@Composable
fun EvolutionDotsTrack(
    milestones: List<Int>,
    currentDay: Int,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        milestones.forEach { milestone ->
            val reached = currentDay >= milestone
            val dotSize = if (reached) 14.dp else 10.dp
            Box(
                modifier = Modifier
                    .height(dotSize)
                    .width(dotSize)
                    .clip(RoundedCornerShape(50))
                    .background(if (reached) OrangePrimary else PeachSurface)
            )
        }
    }
}
