package com.example.capturaprospectos.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import java.util.concurrent.atomic.AtomicBoolean

class NetworkMonitor(
    private val context: Context,
    private val onInternetAvailable: (() -> Unit)? = null
) {
    private val appContext = context.applicationContext
    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var isRegistered = false
    private val triggeredForCurrentSession = AtomicBoolean(false)

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            if (caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                dispararSync()
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (hasInternet && isValidated) {
                dispararSync()
            }
        }

        override fun onLost(network: Network) {
            triggeredForCurrentSession.set(false)
        }
    }

    private fun dispararSync() {
        if (triggeredForCurrentSession.compareAndSet(false, true)) {
            SyncScheduler.encolarSincronizacion(appContext)
            onInternetAvailable?.invoke()
        }
    }

    fun start() {
        if (isRegistered) return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        runCatching {
            connectivityManager.registerNetworkCallback(request, callback)
            isRegistered = true
        }
    }

    fun stop() {
        if (!isRegistered) return
        runCatching {
            connectivityManager.unregisterNetworkCallback(callback)
        }
        isRegistered = false
        triggeredForCurrentSession.set(false)
    }
}