package com.huevo.app.ui.screens.patrones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huevo.app.data.RiskMoment
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.theme.BrownText
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun PatronesScreen(mainViewModel: MainViewModel, onBack: () -> Unit) {
    val insights by mainViewModel.patternInsights.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Atrás", tint = BrownText)
            }
            Text("Tus patrones", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(12.dp))

        HuevoCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = OrangePrimary)
                Text("Hemos detectado algunos patrones.", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "Conocerlos te ayuda a tomar mejores decisiones.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Momentos de mayor riesgo", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (insights.riskMoments.isEmpty()) {
            Text(
                "Registra tus impulsos en la sección Impulso para descubrir tus patrones.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                insights.riskMoments.forEach { moment ->
                    RiskMomentRow(moment)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HuevoCard(containerColor = PeachSurface) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = OrangePrimary)
                Text("Consejo personalizado", style = MaterialTheme.typography.titleMedium)
            }
            Text(insights.advice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun RiskMomentRow(moment: RiskMoment) {
    HuevoCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                when {
                    moment.label.contains("noche", ignoreCase = true) -> Icons.Filled.DarkMode
                    moment.label.contains("móvil", ignoreCase = true) -> Icons.Filled.PhoneAndroid
                    else -> Icons.Filled.Search
                },
                contentDescription = null,
                tint = OrangePrimary
            )
            Text(moment.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }
    }
}
