package com.example.capturaprospectos.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


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
    val fechaCaptura: String,
    val estado: String = ESTADO_PENDIENTE,
    val emailCaptador: String = ""
) {
    companion object {
        const val ESTADO_PENDIENTE = "PENDIENTE"
        const val ESTADO_SINCRONIZADO = "SINCRONIZADO"
    }
}
