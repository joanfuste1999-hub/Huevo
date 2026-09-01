package com.huevo.app.ui.screens.progreso

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huevo.app.data.local.CheckInEntity
import com.huevo.app.model.CheckInResult
import com.huevo.app.model.CompanionExpression
import com.huevo.app.model.CompanionStage
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.companion.CompanionView
import com.huevo.app.ui.components.EvolutionDotsTrack
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.components.HuevoProgressBar
import com.huevo.app.ui.components.StatTile
import com.huevo.app.ui.theme.Danger
import com.huevo.app.ui.theme.SuccessGreen
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProgresoScreen(mainViewModel: MainViewModel, onOpenPatrones: () -> Unit) {
    val state by mainViewModel.appState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Tu progreso", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile(
                    icon = Icons.Filled.LocalFireDepartment,
                    value = "${state.currentStreakDays} días",
                    label = "Racha actual",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    icon = Icons.Filled.EmojiEvents,
                    value = "${state.bestStreakDays} días",
                    label = "Mejor marca",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            HuevoCard {
                Text("Tu compañero", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CompanionView(
                        stage = state.stage,
                        expression = CompanionExpression.CURIOUS,
                        modifier = Modifier.size(88.dp)
                    )
                    Column {
                        Text(state.stage.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(state.stage.subtitle, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                EvolutionDotsTrack(
                    milestones = CompanionStage.entries.map { it.minDay },
                    currentDay = state.currentStreakDays
                )
                HuevoProgressBar(progress = state.progressToNextStageFraction)
            }
        }

        item {
            HuevoCard {
                Text("Objetivo actual", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${state.currentStreakDays} / ${state.objectiveDays} días",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HuevoProgressBar(progress = state.objectiveProgressFraction)
            }
        }

        item {
            HuevoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("Tus patrones", style = MaterialTheme.typography.titleMedium)
                    }
                    androidx.compose.material3.TextButton(onClick = onOpenPatrones) {
                        Text("Ver más")
                    }
                }
                Text(
                    "Descubre cuándo y por qué sueles tener impulsos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Text("Historial reciente", style = MaterialTheme.typography.titleMedium)
        }

        if (state.recentCheckIns.isEmpty()) {
            item {
                Text(
                    "Aún no tienes registros. ¡Empieza hoy!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(state.recentCheckIns) { checkIn ->
                HistoryRow(checkIn)
            }
        }
    }
}

@Composable
private fun HistoryRow(checkIn: CheckInEntity) {
    val date = LocalDate.ofEpochDay(checkIn.epochDay)
    val success = checkIn.result == CheckInResult.SUCCESS
    HuevoCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                if (success) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                tint = if (success) SuccessGreen else Danger
            )
            Column {
                Text(
                    date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(if (success) "Éxito" else "Caída registrada", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
