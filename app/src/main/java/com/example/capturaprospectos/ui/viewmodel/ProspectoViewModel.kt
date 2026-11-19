package com.example.capturaprospectos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capturaprospectos.data.repository.ProspectoRepository
import com.example.capturaprospectos.data.room.ProspectoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ProspectoUiState {
    data object Reposo : ProspectoUiState
    data object Cargando : ProspectoUiState
    data class Exito(val mensaje: String) : ProspectoUiState
    data class Error(val mensaje: String) : ProspectoUiState
}

class ProspectoViewModel(
    private val repository: ProspectoRepository
) : ViewModel() {

    val prospectos: StateFlow<List<ProspectoEntity>> =
        repository.observarProspectosLocales()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _uiState = MutableStateFlow<ProspectoUiState>(ProspectoUiState.Reposo)
    val uiState: StateFlow<ProspectoUiState> = _uiState.asStateFlow()

    fun sincronizarDesdeServidor() {
        viewModelScope.launch {
            _uiState.value = ProspectoUiState.Cargando
            repository.obtenerProspectosRemotos()
                .onSuccess { _uiState.value = ProspectoUiState.Exito("Prospectos actualizados") }
                .onFailure { _uiState.value = ProspectoUiState.Error(it.message ?: "Error al sincronizar") }
        }
    }

    fun guardarProspecto(prospecto: ProspectoEntity) {
        viewModelScope.launch {
            _uiState.value = ProspectoUiState.Cargando
            val resultado = if (prospecto.id == 0L) {
                repository.crearProspecto(prospecto)
            } else {
                repository.actualizarProspecto(prospecto.id, prospecto)
            }
            resultado
                .onSuccess { _uiState.value = ProspectoUiState.Exito("Prospecto guardado") }
                .onFailure { _uiState.value = ProspectoUiState.Error(it.message ?: "Error al guardar") }
        }
    }

    fun eliminarProspecto(prospecto: ProspectoEntity) {
        viewModelScope.launch {
            _uiState.value = ProspectoUiState.Cargando
            repository.eliminarProspecto(prospecto)
                .onSuccess { _uiState.value = ProspectoUiState.Exito("Prospecto eliminado") }
                .onFailure { _uiState.value = ProspectoUiState.Error(it.message ?: "Error al eliminar") }
        }
    }

    fun limpiarEstado() {
        _uiState.value = ProspectoUiState.Reposo
    }
}