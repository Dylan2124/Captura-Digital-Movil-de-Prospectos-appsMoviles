package com.example.capturaprospectos.repository

import com.example.capturaprospectos.data.network.ApiService
import com.example.capturaprospectos.data.network.UsuarioRegistroRequest
import com.example.capturaprospectos.data.room.UsuarioDao
import com.example.capturaprospectos.data.room.UsuarioEntity

class UsuarioRepository(
    private val apiService: ApiService,
    private val usuarioDao: UsuarioDao
) {

    suspend fun iniciarSesion(correo: String, password: String): Result<UsuarioEntity> {
        val correoLimpio = correo.trim().lowercase()
        val usuarioLocal = usuarioDao.obtenerPorEmail(correoLimpio)
            ?: return Result.failure(Exception("Usuario no encontrado"))

        return if (usuarioLocal.passwordHash == password) {
            Result.success(usuarioLocal)
        } else {
            Result.failure(Exception("Contraseña incorrecta"))
        }
    }

    suspend fun registrarUsuario(
        correo: String,
        password: String,
        nombre: String,
        apellido: String = "",
        fechaNacimiento: String = ""
    ): Result<UsuarioEntity> {
        val correoLimpio = correo.trim().lowercase()
        val request = UsuarioRegistroRequest(
            correo = correoLimpio,
            password = password,
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            fechaNacimiento = fechaNacimiento.trim()
        )

        val entidadLocal = UsuarioEntity(
            correo = correoLimpio,
            passwordHash = password,
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            fechaNacimiento = fechaNacimiento.trim(),
            aprobado = false
        )

        return try {
            val response = apiService.registrarUsuario(request)
            if (response.isSuccessful) {
                usuarioDao.registrar(entidadLocal)
                Result.success(entidadLocal)
            } else {
                Result.failure(Exception("Error en servidor (${response.code()}): ${response.message()}"))
            }
        } catch (e: Exception) {
            // Respaldo local en Room si aún no está registrado localmente
            val existente = usuarioDao.obtenerPorEmail(correoLimpio)
            if (existente == null) {
                usuarioDao.registrar(entidadLocal)
                Result.success(entidadLocal)
            } else {
                Result.failure(Exception("Sin conexión o error de red: ${e.message}"))
            }
        }
    }

    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.obtenerPorEmail(correo.trim().lowercase())
    }
}