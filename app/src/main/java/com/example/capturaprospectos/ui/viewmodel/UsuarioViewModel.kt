package com.example.capturaprospectos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.capturaprospectos.data.network.RetrofitClient
import com.example.capturaprospectos.data.room.AppDatabase
import com.example.capturaprospectos.data.room.UsuarioEntity
import com.example.capturaprospectos.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UsuarioUiState {
    data object Reposo : UsuarioUiState
    data object Cargando : UsuarioUiState
    data class Autenticado(val usuario: UsuarioEntity) : UsuarioUiState
    data class Registrado(val mensaje: String) : UsuarioUiState
    data class Error(val mensaje: String) : UsuarioUiState
}

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Reposo)
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = UsuarioUiState.Error("Ingresa correo y contraseña")
            return
        }
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Cargando
            repository.iniciarSesion(correo, password)
                .onSuccess { usuario ->
                    _uiState.value = UsuarioUiState.Autenticado(usuario)
                }
                .onFailure { error ->
                    _uiState.value = UsuarioUiState.Error(error.message ?: "Error al iniciar sesión")
                }
        }
    }

    fun registrar(
        correo: String,
        password: String,
        nombre: String,
        apellido: String = "",
        fechaNacimiento: String = ""
    ) {
        if (correo.isBlank() || password.isBlank() || nombre.isBlank()) {
            _uiState.value = UsuarioUiState.Error("Completa los campos obligatorios")
            return
        }
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Cargando
            repository.registrarUsuario(
                correo = correo,
                password = password,
                nombre = nombre,
                apellido = apellido,
                fechaNacimiento = fechaNacimiento
            )
                .onSuccess {
                    _uiState.value = UsuarioUiState.Registrado("Usuario registrado correctamente")
                }
                .onFailure { error ->
                    _uiState.value = UsuarioUiState.Error(error.message ?: "Error al registrar usuario")
                }
        }
    }

    fun limpiarEstado() {
        _uiState.value = UsuarioUiState.Reposo
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                val repo = UsuarioRepository(
                    apiService = RetrofitClient.api,
                    usuarioDao = db.usuarioDao()
                )
                UsuarioViewModel(repo)
            }
        }
    }
}