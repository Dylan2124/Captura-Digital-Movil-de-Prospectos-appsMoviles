package com.example.capturaprospectos.data.sync
import com.example.capturaprospectos.data.sync.SincronizarProspectosWorker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private const val WORK_NAME = "sync_prospectos_pendientes"

    fun encolarSincronizacion(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SincronizarProspectosWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}