package com.huevo.app.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.theme.Danger
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun PerfilScreen(mainViewModel: MainViewModel, onOpenPatrones: () -> Unit) {
    val state by mainViewModel.appState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(PeachSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(44.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                state.profile.name.ifBlank { "Tu perfil" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text("${state.profile.age} años", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))
        HuevoCard {
            InfoRow("Mi objetivo", "${state.objectiveDays} días")
            InfoRow("Mejor marca", "${state.bestStreakDays} días")
            InfoRow("Mi compañero", "${state.stage.title}")
        }

        Spacer(modifier = Modifier.height(20.dp))
        HuevoCard {
            SettingsRow(Icons.Filled.Insights, "Tus patrones", onOpenPatrones)
            SettingsRow(Icons.Filled.Lock, "Privacidad", {})
            SettingsRow(Icons.Filled.Settings, "Configuración", {})
        }

        Spacer(modifier = Modifier.height(20.dp))
        HuevoCard {
            SettingsRow(Icons.Filled.DeleteForever, "Eliminar mi cuenta", { showDeleteDialog = true }, danger = true)
        }

        Spacer(modifier = Modifier.height(24.dp))
        PrivacyFooter()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar tu cuenta?") },
            text = { Text("Se borrará todo tu progreso y tus registros de este dispositivo de forma permanente. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    mainViewModel.eraseAllData {}
                }) {
                    Text("Eliminar", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit, danger: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = if (danger) Danger else OrangePrimary)
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = if (danger) Danger else MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PrivacyFooter() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FooterItem(Icons.Filled.Lock, "100% privado", "Tu información es solo tuya. No la compartimos con nadie.")
        FooterItem(Icons.Filled.EmojiEvents, "Tú puedes", "Tienes la fuerza para cambiar. Estamos contigo.")
        FooterItem(Icons.Filled.Flag, "Un día a la vez", "No importa cuántas veces caigas, lo importante es volver a empezar.")
    }
}

@Composable
private fun FooterItem(icon: ImageVector, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
