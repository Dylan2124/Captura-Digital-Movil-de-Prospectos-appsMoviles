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
import com.example.capturaprospectos.views.PendientesScreen

@Composable
fun AppNavigation(navController: NavHostController, appState: AppState) {
    NavHost(
        navController = navController,
        // TEMPORAL: mientras el Login no navegue, se arranca en el menú para poder probar.
        // Cuando el Login esté listo, cambiar a "login".
        startDestination = "menu"
    ) {
        // EQUIPO (Login): al validar credenciales llamar appState.iniciarSesion(email)
        // y luego navController.navigate("menu") { popUpTo("login") { inclusive = true } }
        composable("login") { LoginScreen() }

        composable("menu") { MenuScreen(navController, appState) }                          // MENÚ
        composable("nuevo_formulario") { NuevoFormularioScreen(navController, appState) }   // NUEVO FORMULARIO + VALIDACIÓN
        composable("lista_formularios") { ListaFormulariosScreen(navController, appState) } // LISTA DE FORMULARIOS
        composable("pendientes") { PendientesScreen(navController, appState) }
        // composable("pendientes") { PendientesScreen(navController, appState) }
    }
}
