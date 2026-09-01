package com.huevo.app.ui.screens.impulso

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huevo.app.model.CompanionExpression
import com.huevo.app.model.CompanionStage
import com.huevo.app.model.Feeling
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.companion.CompanionView
import com.huevo.app.ui.components.HuevoCard
import com.huevo.app.ui.components.PrimaryButton
import com.huevo.app.ui.components.SelectableChip
import com.huevo.app.ui.theme.MintAccent
import com.huevo.app.ui.theme.OrangePrimary
import com.huevo.app.ui.theme.PeachSurface

@Composable
fun ImpulsoScreen(mainViewModel: MainViewModel) {
    var selectedFeeling by remember { mutableStateOf<Feeling?>(null) }
    var breathingActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modo impulso", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Estoy aquí para ayudarte.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (breathingActive) {
            BreathingExercise(onFinish = { breathingActive = false })
        } else {
            CompanionView(
                stage = CompanionStage.YOUNG_CHICK,
                expression = CompanionExpression.WORRIED,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            HuevoCard {
                Text("¿Qué estás sintiendo?", style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Feeling.entries) { feeling ->
                        SelectableChip(
                            label = feeling.label,
                            selected = selectedFeeling == feeling,
                            onClick = {
                                selectedFeeling = feeling
                                mainViewModel.logCraving(feeling)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Respira conmigo", onClick = { breathingActive = true })
            Spacer(modifier = Modifier.height(12.dp))
            HuevoCard(containerColor = PeachSurface) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = OrangePrimary)
                    Text("Ejercicios y consejos", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    "Cada impulso pasa en pocos minutos. Aguanta, no estás solo en esto.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BreathingExercise(onFinish: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(scale)
                .background(MintAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Air, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(64.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Inhala... y exhala...",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Sigue el ritmo del círculo durante un minuto.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = "Ya me siento mejor", onClick = onFinish)
    }
}
