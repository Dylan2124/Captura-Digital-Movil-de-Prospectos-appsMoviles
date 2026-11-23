package com.example.capturaprospectos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.capturaprospectos.data.repository.ReferenciaExternaRepository
import com.example.capturaprospectos.data.network.external.ReferenciaExternaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ReferenciaExternaUiState {
    data object Cargando : ReferenciaExternaUiState
    data class Exito(val items: List<ReferenciaExternaDto>) : ReferenciaExternaUiState
    data class Error(val mensaje: String) : ReferenciaExternaUiState
}

class ReferenciaExternaViewModel(
    private val repository: ReferenciaExternaRepository = ReferenciaExternaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReferenciaExternaUiState>(ReferenciaExternaUiState.Cargando)
    val uiState: StateFlow<ReferenciaExternaUiState> = _uiState.asStateFlow()

    init {
        cargarReferencias()
    }

    fun cargarReferencias(limite: Int = 8) {
        viewModelScope.launch {
            _uiState.value = ReferenciaExternaUiState.Cargando
            repository.cargarReferencias(limite = limite)
                .onSuccess { lista ->
                    _uiState.value = ReferenciaExternaUiState.Exito(lista)
                }
                .onFailure { throwable ->
                    _uiState.value = ReferenciaExternaUiState.Error(
                        throwable.message ?: "No se pudo obtener información externa"
                    )
                }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ReferenciaExternaViewModel::class.java)) {
                    return ReferenciaExternaViewModel(ReferenciaExternaRepository()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}