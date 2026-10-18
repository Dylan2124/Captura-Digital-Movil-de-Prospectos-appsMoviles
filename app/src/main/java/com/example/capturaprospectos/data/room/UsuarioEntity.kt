package com.example.capturaprospectos.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String
)