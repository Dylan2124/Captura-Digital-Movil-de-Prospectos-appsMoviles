package com.example.capturaprospectos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DuocColorScheme = darkColorScheme(
    primary = AmarilloDuoc,
    onPrimary = Color.Black,
    secondary = AmarilloDuoc,
    onSecondary = Color.Black,
    background = NegroDuoc,
    surface = TarjetaDuoc,
    onBackground = BlancoTexto,
    onSurface = BlancoTexto,
    error = Color(0xFFFF5252),
    onError = Color.White
)

@Composable
fun CapturaProspectosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivado por defecto para respetar los colores institucionales de Duoc
    content: @Composable () -> Unit
) {
    val colorScheme = DuocColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NegroDuoc.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}