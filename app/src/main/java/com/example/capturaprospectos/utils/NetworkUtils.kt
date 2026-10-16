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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.capturaprospectos.data.AppState

/**
 * Función que verifica si el dispositivo tiene conexión activa a Internet.
 *
 * @param context El contexto de la aplicación o actividad.
 * @return true si hay conexión activa a internet, false de lo contrario.
 */
fun verificarConexionInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

/**
 * Composable que observa el estado de la conexión a Internet en tiempo real
 * y recompone la UI automáticamente ante cualquier cambio de red.
 */
@Composable
fun recordEstadoConexion(): Boolean {
    val context = LocalContext.current
    var estaConectado by remember { mutableStateOf(verificarConexionInternet(context)) }

    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                estaConectado = true
            }

            override fun onLost(network: Network) {
                estaConectado = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val tieneInternet =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                estaConectado = tieneInternet
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(request, callback)

        onDispose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

    return estaConectado
}

/**
 * Componente visual que muestra un mini símbolo de Internet:
 * - Ícono de Wi-Fi en verde si está conectado.
 * - Ícono de Wi-Fi con raya en rojo (WifiOff) si está desconectado.
 */
@Composable
fun IndicadorInternet(
    modifier: Modifier = Modifier,
    estaConectado: Boolean = recordEstadoConexion(),
    mostrarTexto: Boolean = false
) {
    val verdeConectado = Color(0xFF2E7D32) // Verde accesible
    val rojoDesconectado = Color(0xFFD32F2F) // Rojo de advertencia

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (estaConectado) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = if (estaConectado) "Conectado a Internet" else "Sin conexión a Internet",
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

/**
 * Composable invisible que monitorea la conexión a Internet.
 * Cuando el teléfono detecta conexión a Internet, sincroniza automáticamente
 * todos los prospectos pendientes.
 */
@Composable
fun ObservarConexionYAutoSincronizar(appState: AppState) {
    val estaConectado = recordEstadoConexion()

    LaunchedEffect(estaConectado) {
        if (estaConectado && appState.pendientes.isNotEmpty() && !appState.sincronizando) {
            appState.sincronizarPendientes()
        }
    }
}
