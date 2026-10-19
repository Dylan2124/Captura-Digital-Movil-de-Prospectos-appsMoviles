package com.example.capturaprospectos.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.capturaprospectos.data.AppState

fun verificarConexionInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

@Composable
fun recordEstadoConexion(): Boolean {
    val context = LocalContext.current
    var estaConectado by remember { mutableStateOf(verificarConexionInternet(context)) }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { estaConectado = true }
            override fun onLost(network: Network) { estaConectado = false }
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                estaConectado = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }
        val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivityManager?.registerNetworkCallback(request, callback)
        onDispose { connectivityManager?.unregisterNetworkCallback(callback) }
    }
    return estaConectado
}

@Composable
fun IndicadorInternet(
    modifier: Modifier = Modifier,
    estaConectado: Boolean = recordEstadoConexion(),
    mostrarTexto: Boolean = false
) {
    val verdeConectado = Color(0xFF2E7D32)
    val rojoDesconectado = Color(0xFFD32F2F)

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = if (estaConectado) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = null,
            tint = if (estaConectado) verdeConectado else rojoDesconectado,
            modifier = Modifier.size(22.dp)
        )
        if (mostrarTexto) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (estaConectado) "En línea" else "Sin conexión",
                style = MaterialTheme.typography.labelSmall,
                color = if (estaConectado) verdeConectado else rojoDesconectado
            )
        }
    }
}

@Composable
fun ObservarConexionYAutoSincronizar(appState: AppState) {
    val estaConectado = recordEstadoConexion()
    LaunchedEffect(estaConectado) {
        if (estaConectado && appState.pendientes.isNotEmpty() && !appState.sincronizando) {
            appState.sincronizarPendientes()
        }
    }
}

