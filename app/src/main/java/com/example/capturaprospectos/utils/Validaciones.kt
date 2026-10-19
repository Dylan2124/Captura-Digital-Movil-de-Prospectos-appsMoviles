package com.example.capturaprospectos.utils



private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val RUT_FORMATO = Regex("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[0-9kK]$")
private val NOMBRE_REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$")


const val VALIDAR_DIGITO_VERIFICADOR = true

fun esEmailValido(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

fun errorNombre(nombre: String): String? = when {
    nombre.isBlank() -> "Falta ingresar el nombre completo"
    nombre.trim().length < 3 -> "El nombre es demasiado corto"
    !NOMBRE_REGEX.matches(nombre.trim()) -> "Solo se permiten letras y espacios"
    else -> null
}

fun errorCorreo(correo: String): String? = when {
    correo.isBlank() -> "Falta ingresar el correo electrónico"
    !esEmailValido(correo) -> "Formato de correo inválido (ej: usuario@dominio.cl)"
    else -> null
}

/** Deja el RUT como "12345678-5" (sin puntos, DV en mayúscula). */
fun normalizarRut(rut: String): String {
    val limpio = rut.replace(".", "").replace("-", "").trim().uppercase()
    return if (limpio.length < 2) limpio else limpio.dropLast(1) + "-" + limpio.last()
}

private fun calcularDv(cuerpo: String): Char {
    var suma = 0
    var mult = 2
    for (c in cuerpo.reversed()) {
        suma += c.digitToInt() * mult
        mult = if (mult == 7) 2 else mult + 1
    }
    return when (val r = 11 - suma % 11) {
        11 -> '0'
        10 -> 'K'
        else -> r.toString()[0]
    }
}

fun errorRut(rut: String): String? {
    if (rut.isBlank()) return "Falta ingresar el RUT"
    if (!RUT_FORMATO.matches(rut.trim())) return "Formato de RUT inválido (ej: 12.345.678-9)"
    val limpio = normalizarRut(rut)
    val cuerpo = limpio.substringBefore("-")
    val dv = limpio.substringAfter("-").first()
    if (cuerpo.length !in 7..8) return "El RUT debe tener 7 u 8 dígitos antes del guion"
    if (VALIDAR_DIGITO_VERIFICADOR && calcularDv(cuerpo) != dv) return "El dígito verificador no corresponde"
    return null
}

fun errorTelefono(telefono: String): String? = when {
    telefono.isBlank() -> "Falta ingresar el teléfono"
    !telefono.all { it.isDigit() } -> "Solo se permiten números"
    telefono.length != 9 -> "El teléfono debe tener 9 dígitos"
    else -> null
}
