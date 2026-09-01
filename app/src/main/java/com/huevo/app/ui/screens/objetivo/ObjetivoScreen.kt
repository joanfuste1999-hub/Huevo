package com.huevo.app.ui.screens.objetivo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.components.HuevoProgressBar
import com.huevo.app.ui.components.PrimaryButton
import com.huevo.app.ui.components.SecondaryButton
import com.huevo.app.ui.components.SelectableChip
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun ObjetivoScreen(mainViewModel: MainViewModel) {
    val state by mainViewModel.appState.collectAsState()
    var editing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tu objetivo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(PeachSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(56.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "${state.currentStreakDays} / ${state.objectiveDays} días",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (state.daysRemainingToObjective > 0) "Te quedan ${state.daysRemainingToObjective} días." else "¡Has alcanzado tu objetivo!",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        HuevoProgressBar(progress = state.objectiveProgressFraction, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))
        HuevoCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.EmojiObjects, contentDescription = null, tint = OrangePrimary)
                Text("Tu compromiso", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "Cada día es una oportunidad para ser tu mejor versión.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (editing) {
            HuevoCard {
                Text("Elige un nuevo objetivo", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(7, 14, 30, 60).forEach { days ->
                        SelectableChip(
                            label = "$days días",
                            selected = state.objectiveDays == days,
                            onClick = { mainViewModel.setObjectiveDays(days) }
                        )
                    }
                }
                PrimaryButton(text = "Guardar", onClick = { editing = false })
            }
        } else {
            SecondaryButton(text = "Cambiar objetivo", onClick = { editing = true })
        }
    }
}
