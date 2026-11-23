package com.example.capturaprospectos.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.capturaprospectos.data.network.RetrofitClient
import com.example.capturaprospectos.data.network.toRequest
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.data.room.ProspectoEntity
import com.example.capturaprospectos.data.room.UsuarioEntity
import com.example.capturaprospectos.data.sync.SyncScheduler
import com.example.capturaprospectos.utils.normalizarRut
import com.example.capturaprospectos.utils.verificarConexionInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppState(private val db: AppDatabase) {

    var captadorActual: String? by mutableStateOf(null)
        private set

    fun iniciarSesion(email: String) { captadorActual = email }
    fun logout() { captadorActual = null }

    suspend fun verificarSiExisteUsuario(email: String): Boolean {
        return db.usuarioDao().obtenerPorEmail(email) != null
    }

    suspend fun registrarNuevoUsuario(
        email: String,
        pass: String,
        nombre: String,
        apellido: String,
        fechaNacimiento: String
    ): Boolean {
        val nuevo = UsuarioEntity(email, pass, nombre, apellido, fechaNacimiento)
        val resultado = db.usuarioDao().registrar(nuevo)
        return resultado != -1L
    }

    suspend fun validarCredenciales(email: String, pass: String): Boolean {
        val usuario = db.usuarioDao().obtenerPorEmail(email)
        return usuario != null && usuario.passwordHash == pass
    }

    val prospectos = mutableStateListOf<ProspectoEntity>()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun cargarProspectos() {
        scope.launch {
            val lista = db.prospectoDao().obtenerTodos()
            withContext(Dispatchers.Main) {
                prospectos.clear()
                prospectos.addAll(lista)
            }
        }
    }

    fun onNetworkAvailable(context: Context) {
        SyncScheduler.encolarSincronizacion(context.applicationContext)
        scope.launch {
            kotlinx.coroutines.delay(2500)
            cargarProspectos()
        }
    }

    fun contarPendientes(): Int =
        prospectos.count { it.estado == ProspectoEntity.ESTADO_PENDIENTE }

    fun guardarProspecto(
        rut: String, nombre: String, correo: String, telefono: String,
        nivel: String, curso: String, modalidad: String, canal: String,
        context: Context
    ): Boolean {
        val rutLimpio = normalizarRut(rut)

        if (prospectos.any { normalizarRut(it.rut) == rutLimpio }) {
            return false
        }

        val appContext = context.applicationContext
        val estadoConectado = verificarConexionInternet(appContext)
        val fecha = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val estadoInicial = if (estadoConectado) {
            ProspectoEntity.ESTADO_SINCRONIZADO
        } else {
            ProspectoEntity.ESTADO_PENDIENTE
        }

        val entidad = ProspectoEntity(
            rut = rutLimpio,
            nombre = nombre.trim(),
            correo = correo.trim(),
            telefono = telefono,
            nivelEducativo = nivel,
            curso = curso,
            modalidad = modalidad,
            canalContacto = canal,
            fechaCaptura = fecha,
            estado = estadoInicial,
            emailCaptador = captadorActual ?: ""
        )

        scope.launch {
            val id = db.prospectoDao().insertar(entidad)
            val guardadoLocal = entidad.copy(id = id)
            var estadoFinal = estadoInicial

            if (estadoConectado) {
                val enviado = enviarAlServidor(guardadoLocal, appContext)
                estadoFinal = if (enviado) {
                    ProspectoEntity.ESTADO_SINCRONIZADO
                } else {
                    ProspectoEntity.ESTADO_PENDIENTE
                }
                db.prospectoDao().actualizarEstado(id, estadoFinal)
            }

            SyncScheduler.encolarSincronizacion(appContext)

            withContext(Dispatchers.Main) {
                prospectos.add(0, guardadoLocal.copy(estado = estadoFinal))
            }
        }
        return true
    }

    fun eliminarProspectos(ids: List<Long>) {
        scope.launch {
            for (id in ids) {
                db.prospectoDao().eliminarPorId(id)
                runCatching {
                    val resp = RetrofitClient.api.eliminarProspecto(id)
                    resp.isSuccessful || resp.code() == 404
                }
            }
            withContext(Dispatchers.Main) {
                prospectos.removeAll { it.id in ids }
            }
        }
    }

    val pendientes: List<ProspectoEntity>
        get() = prospectos.filter { it.estado == ProspectoEntity.ESTADO_PENDIENTE }

    var sincronizando by mutableStateOf(false)
        private set

    fun sincronizarPendientes(
        context: Context? = null,
        onTerminado: (enviados: Int, fallidos: Int) -> Unit = { _, _ -> }
    ) {
        if (sincronizando) return
        val lista = pendientes
        if (lista.isEmpty()) {
            onTerminado(0, 0)
            return
        }

        if (context != null && !verificarConexionInternet(context)) {
            onTerminado(0, lista.size)
            return
        }

        sincronizando = true
        scope.launch {
            var enviados = 0
            var fallidos = 0
            for (p in lista) {
                if (enviarAlServidor(p, context)) {
                    db.prospectoDao().actualizarEstado(p.id, ProspectoEntity.ESTADO_SINCRONIZADO)
                    withContext(Dispatchers.Main) {
                        val i = prospectos.indexOfFirst { it.id == p.id }
                        if (i >= 0) {
                            prospectos[i] = prospectos[i].copy(estado = ProspectoEntity.ESTADO_SINCRONIZADO)
                        }
                    }
                    enviados++
                } else {
                    fallidos++
                    break
                }
            }
            sincronizando = false
            withContext(Dispatchers.Main) { onTerminado(enviados, fallidos) }
        }
    }

    private suspend fun enviarAlServidor(p: ProspectoEntity, context: Context? = null): Boolean {
        if (context != null && !verificarConexionInternet(context)) return false
        return try {
            val response = RetrofitClient.api.crearProspecto(p.toRequest())
            if (response.isSuccessful) {
                true
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                response.code() == 400 &&
                        errorBody.contains("Ya existe un prospecto con el RUT", ignoreCase = true)
            }
        } catch (_: Exception) {
            false
        }
    }
}