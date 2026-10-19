package com.example.capturaprospectos.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TarjetaBorde
import com.example.capturaprospectos.ui.theme.TextoSecundario

/** Pantalla 2 del prototipo: MENÚ (RF - Menú de Navegación Principal). */
@Composable
fun MenuScreen(navController: NavHostController, appState: AppState) {


    val nombre = appState.captadorActual?.substringBefore("@") ?: "Captador"
    val pendientes = appState.contarPendientes()

    fun cerrarSesion() {
        appState.logout()
        navController.navigate("login") { popUpTo("login") { inclusive = true } }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("¿Qué deseas hacer hoy?", color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                }
                TextButton(onClick = { cerrarSesion() }) { Text("Salir", color = Acento) }
            }
            Spacer(Modifier.height(28.dp))

            OpcionMenu(
                icono = Icons.Default.Add,
                titulo = "Nuevo formulario",
                descripcion = "Capturar un nuevo prospecto",
                onClick = { navController.navigate("nuevo_formulario") }
            )
            Spacer(Modifier.height(12.dp))
            OpcionMenu(
                icono = Icons.AutoMirrored.Filled.List,
                titulo = "Lista de formularios",
                descripcion = "Ver, filtrar y gestionar prospectos",
                onClick = { navController.navigate("lista_formularios") }
            )
            Spacer(Modifier.height(12.dp))
            OpcionMenu(
                icono = Icons.Default.Refresh,
                titulo = "Pendientes de sincronizar",
                descripcion = "Formularios aún sin ser enviados",
                contador = pendientes,
                onClick = { navController.navigate("pendientes") }
            )
        }
    }
}

@Composable
private fun OpcionMenu(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    contador: Int = 0
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Tarjeta,
        border = BorderStroke(1.dp, TarjetaBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = Acento)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold)
                Text(descripcion, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            if (contador > 0) {
                Surface(shape = CircleShape, color = Acento) {
                    Text(
                        contador.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
