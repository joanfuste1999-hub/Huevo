package com.huevo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huevo.app.model.CompanionExpression
import com.huevo.app.model.CompanionStage
import com.huevo.app.ui.MainViewModel
import com.huevo.app.ui.companion.CompanionView
import com.huevo.app.ui.navigation.Destination
import com.huevo.app.ui.navigation.HuevoBottomNavBar
import com.huevo.app.ui.navigation.bottomNavItems
import com.huevo.app.ui.onboarding.OnboardingScreen
import com.huevo.app.ui.screens.home.HoyScreen
import com.huevo.app.ui.screens.impulso.ImpulsoScreen
import com.huevo.app.ui.screens.objetivo.ObjetivoScreen
import com.huevo.app.ui.screens.patrones.PatronesScreen
import com.huevo.app.ui.screens.perfil.PerfilScreen
import com.huevo.app.ui.screens.progreso.ProgresoScreen
import com.huevo.app.ui.theme.HuevoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuevoTheme {
                HuevoApp()
            }
        }
    }
}

@Composable
fun HuevoApp() {
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as HuevoApplication
    val onboardingAnswers by application.repository.onboardingAnswers.collectAsState(initial = null)

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val answers = onboardingAnswers
        when {
            answers == null -> LoadingSplash()
            !answers.completed -> OnboardingScreen(onFinished = {})
            else -> MainNavHost()
        }
    }
}

@Composable
private fun LoadingSplash() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CompanionView(
            stage = CompanionStage.EGG,
            expression = CompanionExpression.SLEEPY,
            modifier = Modifier.padding(48.dp)
        )
    }
}

@Composable
private fun MainNavHost() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.destination.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                HuevoBottomNavBar(currentRoute = currentRoute) { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Hoy.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Hoy.route) { HoyScreen(mainViewModel) }
            composable(Destination.Progreso.route) {
                ProgresoScreen(mainViewModel, onOpenPatrones = { navController.navigate(Destination.Patrones.route) })
            }
            composable(Destination.Impulso.route) { ImpulsoScreen(mainViewModel) }
            composable(Destination.Objetivo.route) { ObjetivoScreen(mainViewModel) }
            composable(Destination.Perfil.route) {
                PerfilScreen(
                    mainViewModel,
                    onOpenPatrones = { navController.navigate(Destination.Patrones.route) }
                )
            }
            composable(Destination.Patrones.route) {
                PatronesScreen(mainViewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
