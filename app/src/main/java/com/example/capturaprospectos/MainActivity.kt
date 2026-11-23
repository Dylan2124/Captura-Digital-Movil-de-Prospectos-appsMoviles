package com.example.capturaprospectos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.data.sync.NetworkMonitor
import com.example.capturaprospectos.data.sync.SyncScheduler
import com.example.capturaprospectos.navigation.AppNavigation
import com.example.capturaprospectos.ui.theme.CapturaProspectosTheme

class MainActivity : ComponentActivity() {

    private lateinit var networkMonitor: NetworkMonitor
    private var appStateRef: AppState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContext = applicationContext
        val db = AppDatabase.getInstance(appContext)

        networkMonitor = NetworkMonitor(appContext) {
            appStateRef?.onNetworkAvailable(appContext)
        }
        SyncScheduler.encolarSincronizacion(appContext)

        setContent {
            CapturaProspectosTheme {
                val navController = rememberNavController()
                val appState = remember { AppState(db) }.also {
                    appStateRef = it
                }

                LaunchedEffect(appState) {
                    appState.cargarProspectos()
                }

                AppNavigation(
                    navController = navController,
                    appState = appState
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        networkMonitor.start()
    }

    override fun onStop() {
        networkMonitor.stop()
        super.onStop()
    }
}