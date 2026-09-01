package com.huevo.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String) {
    data object Onboarding : Destination("onboarding")
    data object Hoy : Destination("hoy")
    data object Progreso : Destination("progreso")
    data object Impulso : Destination("impulso")
    data object Objetivo : Destination("objetivo")
    data object Patrones : Destination("patrones")
    data object Perfil : Destination("perfil")
}

data class BottomNavItem(val destination: Destination, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Destination.Hoy, "Hoy", Icons.Filled.Home),
    BottomNavItem(Destination.Progreso, "Progreso", Icons.Filled.ShowChart),
    BottomNavItem(Destination.Impulso, "Impulso", Icons.Filled.Bolt),
    BottomNavItem(Destination.Objetivo, "Objetivo", Icons.Filled.TrackChanges),
    BottomNavItem(Destination.Perfil, "Perfil", Icons.Filled.Person)
)
