package com.example.capturaprospectos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema único de la app (oscuro, como el prototipo). Se quitó el color dinámico de Android 12+
// porque pisaba la paleta turquesa del diseño.
private val EsquemaOscuro = darkColorScheme(
    primary = Acento,
    onPrimary = Color(0xFF04201C),
    background = Fondo,
    surface = Tarjeta,
    onSurface = Color(0xFFE6EAF2),
    onBackground = Color(0xFFE6EAF2),
    error = ErrorNaranja,
    outline = TarjetaBorde
)

@Composable
fun CapturaProspectosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        typography = Typography,
        content = content
    )
}
