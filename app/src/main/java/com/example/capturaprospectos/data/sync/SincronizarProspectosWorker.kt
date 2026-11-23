package com.example.capturaprospectos.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.capturaprospectos.data.network.RetrofitClient
import com.example.capturaprospectos.data.repository.ProspectoRepository
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.data.room.ProspectoEntity

class SincronizarProspectosWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.prospectoDao()
        val repository = ProspectoRepository(
            apiService = RetrofitClient.api,
            prospectoDao = dao
        )

        val pendientes = dao.obtenerPorEstado(ProspectoEntity.ESTADO_PENDIENTE)
        if (pendientes.isEmpty()) return Result.success()

        var huboErrorTransitorio = false

        for (prospecto in pendientes) {
            val res = repository.crearProspecto(prospecto)
            if (res.isFailure) {
                huboErrorTransitorio = true
            }
        }

        return if (huboErrorTransitorio) Result.retry() else Result.success()
    }
}