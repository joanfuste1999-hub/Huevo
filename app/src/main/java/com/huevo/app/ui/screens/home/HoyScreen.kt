package com.huevo.app.ui.screens.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsNone
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
import com.huevo.app.model.CompanionExpression
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.companion.CompanionView
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.components.HuevoProgressBar
import com.huevo.app.ui.components.PrimaryButton
import com.huevo.app.ui.components.SecondaryButton
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface
import com.huevo.app.ui.theme.SuccessGreen
import com.huevo.app.ui.tips.Tips
import java.time.LocalTime

@Composable
fun HoyScreen(mainViewModel: MainViewModel) {
    val state by mainViewModel.appState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${timeOfDayGreeting()}${if (state.profile.name.isNotBlank()) ", ${state.profile.name}" else ""}! 👋",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Un día a la vez, tú tienes el control.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            HuevoCard {
                Text("Tu compañero", style = MaterialTheme.typography.titleMedium)
                CompanionView(
                    stage = state.stage,
                    expression = expressionFor(state),
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(180.dp)
                        .padding(vertical = 8.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${state.currentStreakDays} días",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(state.stage.subtitle, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Progreso hacia la siguiente evolución", style = MaterialTheme.typography.bodySmall)
                HuevoProgressBar(progress = state.progressToNextStageFraction)
                Text(
                    state.progressToNextStageLabel,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            HuevoCard {
                Text("¿Cómo te ha ido hoy?", style = MaterialTheme.typography.titleMedium)
                if (state.alreadyCheckedInToday) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        Text("Ya has registrado tu día de hoy. ¡Vuelve mañana!", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    PrimaryButton(text = "Lo he conseguido", onClick = { mainViewModel.checkInSuccess() })
                    SecondaryButton(text = "He tenido una caída", onClick = { mainViewModel.checkInRelapse() })
                }
            }
        }

        item {
            HuevoCard(containerColor = PeachSurface) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = OrangePrimary)
                    Text("Consejo del día", style = MaterialTheme.typography.titleMedium)
                }
                Text(Tips.tipForToday(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private fun timeOfDayGreeting(): String {
    val hour = LocalTime.now().hour
    return when {
        hour < 12 -> "Buenos días"
        hour < 20 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

private fun expressionFor(state: com.huevo.app.data.AppState): CompanionExpression = when {
    state.alreadyCheckedInToday && state.currentStreakDays >= state.stage.minDay + 3 -> CompanionExpression.PROUD
    state.alreadyCheckedInToday -> CompanionExpression.HAPPY
    LocalTime.now().hour >= 22 || LocalTime.now().hour < 6 -> CompanionExpression.SLEEPY
    else -> CompanionExpression.MOTIVATED
}
