package com.example.capturaprospectos

import com.example.capturaprospectos.utils.*
import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {
    @Test fun correo() {
        assertNull(errorCorreo("usuario@dominio.cl"))
        assertNotNull(errorCorreo("F"))
        assertNotNull(errorCorreo(""))
    }
    @Test fun rut() {
        assertNull(errorRut("12.345.678-5"))
        assertNotNull(errorRut("123456785")) // Sin guion ahora debe fallar
        assertNull(errorRut("11111111-1"))
        assertNull(errorRut("12.345.678-9"))
        assertNotNull(errorRut("abc"))
        assertNotNull(errorRut(""))
    }
    @Test fun telefono() {
        assertNull(errorTelefono("912345678"))
        assertNotNull(errorTelefono("9123"))
        assertNotNull(errorTelefono("91234567a"))
    }
    @Test fun nombre() {
        assertNull(errorNombre("Juan García"))
        assertNotNull(errorNombre("J4"))
        assertNotNull(errorNombre(" "))
    }
}
