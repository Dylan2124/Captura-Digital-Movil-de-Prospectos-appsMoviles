package com.example.capturaprospectos.data.repository

import com.example.capturaprospectos.data.network.external.ExternalApiService
import com.example.capturaprospectos.data.network.external.ExternalRetrofitClient
import com.example.capturaprospectos.data.network.external.ReferenciaExternaDto

class ReferenciaExternaRepository(
    private val apiService: ExternalApiService = ExternalRetrofitClient.api
) {

    suspend fun cargarReferencias(limite: Int = 8): Result<List<ReferenciaExternaDto>> = runCatching {
        val response = apiService.obtenerGuiasReferencia(limite = limite)
        if (!response.isSuccessful) {
            error("Error al consultar API externa (${response.code()})")
        }
        response.body().orEmpty()
    }
}