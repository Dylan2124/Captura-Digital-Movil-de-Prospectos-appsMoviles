package com.example.capturaprospectos.data.network.external

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalApiService {

    @GET("posts")
    suspend fun obtenerGuiasReferencia(
        @Query("_limit") limite: Int = 8
    ): Response<List<ReferenciaExternaDto>>
}