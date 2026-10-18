package com.example.capturaprospectos.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.data.room.ProspectoEntity
import com.example.capturaprospectos.data.room.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AppState(private val db: AppDatabase) {

    // ---- Sesión: lo completa quien haga el Login (llamar iniciarSesion al validar credenciales) ----
    var captadorActual: String? by mutableStateOf(null)
        private set

    fun iniciarSesion(email: String) { captadorActual = email }
    fun logout() { captadorActual = null }

    // ---- Usuarios y Autenticación ----
    suspend fun verificarSiExisteUsuario(email: String): Boolean {
        return db.usuarioDao().obtenerPorEmail(email) != null
    }

    suspend fun registrarNuevoUsuario(email: String, pass: String, nombre: String, apellido: String, fechaNacimiento: String): Boolean {
        val nuevo = UsuarioEntity(email, pass, nombre, apellido, fechaNacimiento)
        val resultado = db.usuarioDao().registrar(nuevo)
        return resultado != -1L // Retorna true si se guardó con éxito
    }

    suspend fun validarCredenciales(email: String, pass: String): Boolean {
        val usuario = db.usuarioDao().obtenerPorEmail(email)
        return usuario != null && usuario.passwordHash == pass
    }

    // ---- Prospectos ----
    val prospectos = mutableStateListOf<ProspectoEntity>()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun cargarProspectos() {
        scope.launch {
            val lista = db.prospectoDao().obtenerTodos()
            prospectos.clear()
            prospectos.addAll(lista)
        }
    }

    fun contarPendientes(): Int =
        prospectos.count { it.estado == ProspectoEntity.ESTADO_PENDIENTE }

    /** Devuelve false si el RUT ya existe (RF01: ID duplicado). Fecha y estado son automáticos (RF02). */
    fun guardarProspecto(
        rut: String, nombre: String, correo: String, telefono: String,
        nivel: String, curso: String, modalidad: String, canal: String
    ): Boolean {
        if (prospectos.any { it.rut == rut }) return false
        val fecha = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val entidad = ProspectoEntity(
            rut = rut, nombre = nombre.trim(), correo = correo.trim(), telefono = telefono,
            nivelEducativo = nivel, curso = curso, modalidad = modalidad, canalContacto = canal,
            fechaCaptura = fecha, emailCaptador = captadorActual ?: ""
        )
        scope.launch {
            val id = db.prospectoDao().insertar(entidad)
            prospectos.add(0, entidad.copy(id = id))
        }
        return true
    }
    // Solo los registros que aún no se enviaron al servidor
    val pendientes: List<ProspectoEntity>
        get() = prospectos.filter { it.estado == ProspectoEntity.ESTADO_PENDIENTE }

    var sincronizando by mutableStateOf(false)
        private set

    /**
     * Envía los pendientes uno a uno. Si uno falla, se detiene y los demás
     * siguen PENDIENTE para el próximo intento (RF04).
     */
    fun sincronizarPendientes(onTerminado: (enviados: Int, fallidos: Int) -> Unit = { _, _ -> }) {
        if (sincronizando) return
        val lista = pendientes
        if (lista.isEmpty()) { onTerminado(0, 0); return }
        sincronizando = true
        scope.launch {
            var enviados = 0
            var fallidos = 0
            for (p in lista) {
                if (enviarAlServidor(p)) {
                    db.prospectoDao().actualizarEstado(p.id, ProspectoEntity.ESTADO_SINCRONIZADO)
                    val i = prospectos.indexOfFirst { it.id == p.id }
                    if (i >= 0) prospectos[i] = prospectos[i].copy(estado = ProspectoEntity.ESTADO_SINCRONIZADO)
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

    // TODO (quien haga Retrofit): reemplazar por la llamada real a la API Spring Boot.
// Debe devolver true solo si el servidor respondió OK.
    private suspend fun enviarAlServidor(p: ProspectoEntity): Boolean {
        delay(400) // simulación mientras no exista la API
        return true
    }

    fun eliminarProspectos(ids: List<Long>) {
        scope.launch {
            for (id in ids) {
                db.prospectoDao().eliminar(id)
            }
            withContext(Dispatchers.Main) {
                prospectos.removeAll { it.id in ids }
            }
        }
    }
}
