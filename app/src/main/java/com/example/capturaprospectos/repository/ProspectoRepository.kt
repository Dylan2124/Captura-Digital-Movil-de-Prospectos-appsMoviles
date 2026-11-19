package com.example.capturaprospectos.data.repository

import com.example.capturaprospectos.data.network.ApiService
import com.example.capturaprospectos.data.network.ProspectoRequest
import com.example.capturaprospectos.data.network.toEntity
import com.example.capturaprospectos.data.network.toRequest
import com.example.capturaprospectos.data.room.ProspectoDao
import com.example.capturaprospectos.data.room.ProspectoEntity
import kotlinx.coroutines.flow.Flow

class ProspectoRepository(
    private val apiService: ApiService,
    private val prospectoDao: ProspectoDao
) {

    fun observarProspectosLocales(): Flow<List<ProspectoEntity>> =
        prospectoDao.observarTodos()

    suspend fun obtenerProspectosRemotos(): Result<List<ProspectoEntity>> = runCatching {
        val response = apiService.obtenerProspectos()
        if (!response.isSuccessful) {
            error("Error al obtener prospectos (${response.code()})")
        }
        val entidades = response.body().orEmpty().map { it.toEntity() }
        entidades.forEach { prospectoDao.insertar(it) }
        entidades
    }

    suspend fun crearProspecto(prospecto: ProspectoEntity): Result<ProspectoEntity> = runCatching {
        val request: ProspectoRequest = prospecto.toRequest()
        val response = apiService.crearProspecto(request)
        if (!response.isSuccessful) {
            error("Error al crear prospecto (${response.code()})")
        }
        val guardado = response.body()?.toEntity(idLocal = prospecto.id)
            ?: prospecto.copy(estado = ProspectoEntity.ESTADO_SINCRONIZADO)
        prospectoDao.insertar(guardado)
        guardado
    }

    suspend fun actualizarProspecto(id: Long, prospecto: ProspectoEntity): Result<ProspectoEntity> = runCatching {
        val response = apiService.actualizarProspecto(id = id, prospecto = prospecto.toRequest())
        if (!response.isSuccessful) {
            error("Error al actualizar prospecto (${response.code()})")
        }
        val actualizado = response.body()?.toEntity(idLocal = id)
            ?: prospecto.copy(id = id, estado = ProspectoEntity.ESTADO_SINCRONIZADO)
        prospectoDao.actualizar(actualizado)
        actualizado
    }

    suspend fun eliminarProspecto(prospecto: ProspectoEntity): Result<Unit> = runCatching {
        if (prospecto.id != 0L) {
            val response = apiService.eliminarProspecto(prospecto.id)
            if (!response.isSuccessful && response.code() != 404) {
                error("Error al eliminar prospecto (${response.code()})")
            }
        }
        prospectoDao.eliminar(prospecto)
    }
}