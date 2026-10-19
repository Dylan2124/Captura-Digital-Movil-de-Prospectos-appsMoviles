package com.example.capturaprospectos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.utils.ObservarConexionYAutoSincronizar
import com.example.capturaprospectos.views.ListaFormulariosScreen
import com.example.capturaprospectos.views.LoginScreen
import com.example.capturaprospectos.views.MenuScreen
import com.example.capturaprospectos.views.NuevoFormularioScreen
import com.example.capturaprospectos.views.PendientesScreen
import com.example.capturaprospectos.views.RegistroScreen

@Composable
fun AppNavigation(navController: NavHostController, appState: AppState) {
    ObservarConexionYAutoSincronizar(appState)
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController, appState) }

        composable("registro") { RegistroScreen(navController, appState) }

        composable("menu") { MenuScreen(navController, appState) }

        composable("nuevo_formulario") { NuevoFormularioScreen(navController, appState) }

        composable("lista_formularios") { ListaFormulariosScreen(navController, appState) }

        composable("pendientes") { PendientesScreen(navController, appState) }
    }
}