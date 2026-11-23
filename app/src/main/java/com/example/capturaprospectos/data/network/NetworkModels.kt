package com.example.capturaprospectos.data.network

import com.example.capturaprospectos.data.room.ProspectoEntity


data class UsuarioRegistroRequest(
    val correo: String,
    val password: String,
    val nombre: String,
    val apellido: String = "",
    val fechaNacimiento: String = ""
)

data class ProspectoRequest(
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val nivelEducativo: String,
    val curso: String,
    val modalidad: String,
    val canalContacto: String,
    val fechaCaptura: String,
    val estado: String = ProspectoEntity.ESTADO_SINCRONIZADO,
    val emailCaptador: String = ""
)

data class ProspectoDto(
    val id: Long? = null,
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val nivelEducativo: String,
    val curso: String,
    val modalidad: String,
    val canalContacto: String,
    val fechaCaptura: String,
    val estado: String = ProspectoEntity.ESTADO_SINCRONIZADO,
    val emailCaptador: String = ""
)

fun ProspectoEntity.toRequest(): ProspectoRequest = ProspectoRequest(
    rut = rut,
    nombre = nombre,
    correo = correo,
    telefono = telefono,
    nivelEducativo = nivelEducativo,
    curso = curso,
    modalidad = modalidad,
    canalContacto = canalContacto,
    fechaCaptura = fechaCaptura,
    estado = estado,
    emailCaptador = emailCaptador
)

fun ProspectoDto.toEntity(idLocal: Long = 0): ProspectoEntity = ProspectoEntity(
    id = if (idLocal != 0L) idLocal else (id ?: 0L),
    rut = rut,
    nombre = nombre,
    correo = correo,
    telefono = telefono,
    nivelEducativo = nivelEducativo,
    curso = curso,
    modalidad = modalidad,
    canalContacto = canalContacto,
    fechaCaptura = fechaCaptura,
    estado = estado.ifBlank { ProspectoEntity.ESTADO_SINCRONIZADO },
    emailCaptador = emailCaptador
)