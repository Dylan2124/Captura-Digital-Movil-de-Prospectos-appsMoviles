package com.example.capturaprospectos.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRegistroRequest
    ): Response<String>

    // --- CRUD de Prospectos contra backend Spring Boot ---
    @GET("api/prospectos")
    suspend fun obtenerProspectos(): Response<List<ProspectoDto>>

    @GET("api/prospectos/{id}")
    suspend fun obtenerProspectoPorId(
        @Path("id") id: Long
    ): Response<ProspectoDto>

    @POST("api/prospectos")
    suspend fun crearProspecto(
        @Body prospecto: ProspectoRequest
    ): Response<ProspectoDto>

    @PUT("api/prospectos/{id}")
    suspend fun actualizarProspecto(
        @Path("id") id: Long,
        @Body prospecto: ProspectoRequest
    ): Response<ProspectoDto>

    @DELETE("api/prospectos/{id}")
    suspend fun eliminarProspecto(
        @Path("id") id: Long
    ): Response<Unit>
}