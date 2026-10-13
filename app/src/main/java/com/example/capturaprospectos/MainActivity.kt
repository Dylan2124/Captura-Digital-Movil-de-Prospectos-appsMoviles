package com.example.capturaprospectos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.navigation.AppNavigation
import com.example.capturaprospectos.ui.theme.CapturaProspectosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val appState = AppState(db)
        appState.cargarProspectos()

        setContent {
            val navController = rememberNavController()
            CapturaProspectosTheme {
                AppNavigation(navController, appState)
            }
        }
    }
}
