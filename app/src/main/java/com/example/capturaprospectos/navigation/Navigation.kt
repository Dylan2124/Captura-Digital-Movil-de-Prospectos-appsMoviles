package com.example.capturaprospectos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.views.ListaFormulariosScreen
import com.example.capturaprospectos.views.LoginScreen
import com.example.capturaprospectos.views.MenuScreen
import com.example.capturaprospectos.views.NuevoFormularioScreen
import com.example.capturaprospectos.views.OnboardingScreen
import com.example.capturaprospectos.views.PendientesScreen
import com.example.capturaprospectos.views.RegistroScreen
import com.example.capturaprospectos.views.SplashScreen

@Composable
fun AppNavigation(navController: NavHostController, appState: AppState) {
    NavHost(
        navController = navController,
        startDestination = "splash" // Inicia con la animación institucional de Duoc UC
    ) {
        // 1. Pantalla de carga / Splash animada
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // 2. Pantallas de Onboarding / Carrusel de bienvenida
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }

        // 3. Pantalla de Inicio de Sesión (Login)
        composable("login") {
            LoginScreen(navController = navController, appState = appState)
        }

        // 4. Pantalla de Registro de cuenta nueva
        composable("registro") {
            RegistroScreen(navController = navController, appState = appState)
        }

        // 5. Menú principal de la aplicación
        composable("menu") {
            MenuScreen(navController = navController, appState = appState)
        }

        // 6. Formulario para registrar un nuevo prospecto
        composable("nuevo_formulario") {
            NuevoFormularioScreen(navController = navController, appState = appState)
        }

        // 7. Lista de formularios con filtros y exportación CSV
        composable("lista_formularios") {
            ListaFormulariosScreen(navController = navController, appState = appState)
        }

        // 8. Pantalla de elementos pendientes o sincronización
        composable("pendientes") {
            PendientesScreen(navController = navController, appState = appState)
        }
    }
}