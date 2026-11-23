package com.example.capturaprospectos.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val correo: String,
    val passwordHash: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String,
    val aprobado: Boolean = false // <--- NUEVO: Requiere validación institucional
)