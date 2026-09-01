package com.huevo.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huevo.app.model.CompanionExpression
import com.huevo.app.model.CompanionStage
import com.huevo.app.model.Frequency
import com.huevo.app.model.Goal
import com.huevo.app.model.Moment
import com.huevo.app.model.PornUsage
import com.huevo.app.model.Trigger
import com.huevo.app.ui.companion.CompanionView
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.components.OnboardingTopBar
import com.huevo.app.ui.components.PrimaryButton
import com.huevo.app.ui.components.SelectableChip
import com.huevo.app.ui.components.SelectableOptionRow
import com.huevo.app.ui.theme.MintAccent
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OnboardingTopBar(
            step = uiState.step,
            totalSteps = ONBOARDING_TOTAL_STEPS,
            onBack = if (uiState.step > 1) viewModel::goBack else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (uiState.step) {
                1 -> StepWelcome()
                2 -> StepName(uiState.answers.name, viewModel::updateName)
                3 -> StepAge(uiState.answers.age, viewModel::updateAge)
                4 -> StepFrequency(uiState.answers.frequency, viewModel::selectFrequency)
                5 -> StepGoals(uiState.answers.goals, viewModel::toggleGoal)
                6 -> StepInitialGoal(uiState.answers.initialGoalDays, viewModel::setInitialGoalDays)
                7 -> StepPornUsage(uiState.answers.pornUsage, viewModel::selectPornUsage)
                8 -> StepMoments(uiState.answers.moments, viewModel::toggleMoment)
                9 -> StepTriggers(uiState.answers.triggers, viewModel::toggleTrigger)
                10 -> StepMeetCompanion()
                11 -> StepAllSet()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = if (uiState.step == 1) "Comenzar" else if (uiState.step == ONBOARDING_TOTAL_STEPS) "Comenzar mi viaje" else "Continuar",
            enabled = uiState.canContinue,
            onClick = {
                if (uiState.step == ONBOARDING_TOTAL_STEPS) {
                    viewModel.finishOnboarding(onFinished)
                } else {
                    viewModel.goNext()
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StepScaffold(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(28.dp))
        content()
    }
}

@Composable
private fun StepWelcome() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(PeachSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.WbSunny, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(80.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Estamos aquí para ayudarte a recuperar el control y sentirte mejor contigo mismo.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepName(name: String, onNameChange: (String) -> Unit) {
    StepScaffold(title = "¿Cómo te llamas?", subtitle = "Puedes usar un apodo si prefieres.") {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tu nombre") },
            singleLine = true
        )
    }
}

@Composable
private fun StepAge(age: Int, onAgeChange: (Int) -> Unit) {
    StepScaffold(title = "¿Qué edad tienes?", subtitle = "Esto nos ayuda a personalizar tu experiencia.") {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (age >= 60) "60+" else "$age",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text("años", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = age.toFloat(),
                onValueChange = { onAgeChange(it.toInt()) },
                valueRange = 15f..60f,
                colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("15", style = MaterialTheme.typography.bodySmall)
                Text("60+", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StepFrequency(selected: Frequency?, onSelect: (Frequency) -> Unit) {
    StepScaffold(title = "¿Con qué frecuencia lo haces actualmente?", subtitle = "Sé honesto, es solo para ti.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Frequency.entries.forEach { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
private fun StepGoals(selected: Set<Goal>, onToggle: (Goal) -> Unit) {
    StepScaffold(title = "¿Cuál es tu objetivo principal?", subtitle = "Elige lo que mejor te represente.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Goal.entries.forEach { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = selected.contains(option),
                    onClick = { onToggle(option) },
                    multiSelect = true
                )
            }
        }
    }
}

@Composable
private fun StepInitialGoal(days: Int, onChange: (Int) -> Unit) {
    StepScaffold(title = "¿Cuál es tu meta inicial?", subtitle = "Puedes cambiarla más adelante.") {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                IconButton(onClick = { if (days > 1) onChange(days - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Menos días", tint = OrangePrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$days", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Text("días", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = { onChange(days + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Más días", tint = OrangePrimary)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(3, 7, 14, 30).forEach { quick ->
                    SelectableChip(
                        label = "$quick días",
                        selected = days == quick,
                        onClick = { onChange(quick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPornUsage(selected: PornUsage?, onSelect: (PornUsage) -> Unit) {
    StepScaffold(title = "¿Usas contenido pornográfico?", subtitle = "Esto nos ayuda a entender mejor tus hábitos.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PornUsage.entries.forEach { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

@Composable
private fun StepMoments(selected: Set<Moment>, onToggle: (Moment) -> Unit) {
    StepScaffold(title = "¿Cuándo suele ocurrir?", subtitle = "Selecciona los momentos que más te identifiquen.") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Moment.entries) { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = selected.contains(option),
                    onClick = { onToggle(option) },
                    multiSelect = true
                )
            }
        }
    }
}

@Composable
private fun StepTriggers(selected: Set<Trigger>, onToggle: (Trigger) -> Unit) {
    StepScaffold(title = "¿Qué situaciones suelen desencadenarlo?", subtitle = "Marca las que más influyen.") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Trigger.entries) { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = selected.contains(option),
                    onClick = { onToggle(option) },
                    multiSelect = true
                )
            }
        }
    }
}

@Composable
private fun StepMeetCompanion() {
    StepScaffold(title = "Conoce a tu compañero", subtitle = "Tu compañero crecerá contigo y reflejará tu progreso cada día.") {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CompanionStage.entries.forEach { stage ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
                        CompanionView(
                            stage = stage,
                            expression = CompanionExpression.HAPPY,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("D${stage.minDay}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            HuevoCard(containerColor = PeachSurface) {
                InfoBullet(Icons.Filled.Favorite, "Tu compañero siempre estará en el mismo nivel que tú.")
                InfoBullet(Icons.Filled.Schedule, "Si tienes una caída, ambos volvéis a empezar.")
                InfoBullet(Icons.Filled.Star, "Cada día cuenta. Sigue adelante, paso a paso.")
            }
        }
    }
}

@Composable
private fun StepAllSet() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        CompanionView(
            stage = CompanionStage.YOUNG_CHICK,
            expression = CompanionExpression.HAPPY,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("¡Todo listo!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Estás preparado para empezar este camino.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        HuevoCard {
            InfoBullet(Icons.Filled.Schedule, "Un día a la vez.")
            InfoBullet(Icons.Filled.Shield, "Sin juicios.")
            InfoBullet(Icons.Filled.EmojiEvents, "Estamos contigo.")
        }
    }
}

@Composable
private fun InfoBullet(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = CircleShape, color = MintAccent, modifier = Modifier.size(32.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
            }
        }
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
