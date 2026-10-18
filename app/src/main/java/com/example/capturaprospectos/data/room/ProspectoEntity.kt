package com.example.capturaprospectos.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "prospectos". El RUT es único (RF01: ID duplicado bloquea el guardado).
// COORDINAR con el compañero que hace Room: si él ya definió esta entidad, usar la suya
// y solo ajustar los nombres de campos en AppState.guardarProspecto().
@Entity(tableName = "prospectos", indices = [Index(value = ["rut"], unique = true)])
data class ProspectoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val nivelEducativo: String,
    val curso: String,
    val modalidad: String,
    val canalContacto: String,
    val fechaCaptura: String,          // RF02: "dd-MM-yyyy HH:mm:ss", la genera el sistema
    val estado: String = ESTADO_PENDIENTE, // RF02: siempre nace "PENDIENTE"
    val emailCaptador: String = ""
) {
    companion object {
        const val ESTADO_PENDIENTE = "PENDIENTE"
        const val ESTADO_SINCRONIZADO = "SINCRONIZADO"
    }
}
