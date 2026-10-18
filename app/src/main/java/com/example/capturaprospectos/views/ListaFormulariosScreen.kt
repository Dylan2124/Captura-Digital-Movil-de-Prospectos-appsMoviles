package com.example.capturaprospectos.views

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.data.room.ProspectoEntity
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TarjetaBorde
import com.example.capturaprospectos.ui.theme.TextoSecundario
import com.example.capturaprospectos.utils.IndicadorInternet

/**
 * Función que activa el motor de vibración nativo del dispositivo Android.
 * Emite un pulso de vibración para alertar al usuario antes de eliminar.
 */
private fun vibrarTelefono(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(350)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaFormulariosScreen(navController: NavHostController, appState: AppState) {
    val context = LocalContext.current
    val prospectos = appState.prospectos
    var filtroFecha by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateMapOf<Long, Boolean>() }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    // Diálogo nativo de confirmación con alerta previa
    if (mostrarDialogoConfirmacion) {
        val cantidad = selectedIds.size
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Está seguro que desea eliminar $cantidad prospecto(s) seleccionado(s)? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idsToDelete = selectedIds.filter { it.value }.keys.toList()
                        appState.eliminarProspectos(idsToDelete)
                        selectedIds.clear()
                        mostrarDialogoConfirmacion = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Filtrar prospectos por fecha ingresada (coincidencia parcial, ej: "12-10-2026")
    val prospectosFiltrados = remember(prospectos, filtroFecha) {
        if (filtroFecha.isBlank()) {
            prospectos
        } else {
            prospectos.filter { it.fechaCaptura.contains(filtroFecha.trim()) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                title = {
                    Column {
                        Text("Lista de formularios", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("${prospectosFiltrados.size} PROSPECTOS", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                    }
                },
                actions = {
                    IndicadorInternet(modifier = Modifier.padding(end = 16.dp))
                }
            )
        },
        bottomBar = {
            if (selectedIds.values.any { it }) {
                Surface(
                    color = Tarjeta,
                    border = BorderStroke(1.dp, TarjetaBorde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val count = selectedIds.values.count { it }
                        Text(
                            "$count seleccionado(s)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                vibrarTelefono(context)
                                mostrarDialogoConfirmacion = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
                            Spacer(Modifier.width(8.dp))
                            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Filtro de fecha
            Text(
                text = "CARGAR HASTA FECHA",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filtroFecha,
                onValueChange = { filtroFecha = it },
                placeholder = { Text("Ej: 12-10-2026", color = TextoSecundario) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = TextoSecundario)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Acento,
                    unfocusedBorderColor = TarjetaBorde,
                    focusedContainerColor = Tarjeta,
                    unfocusedContainerColor = Tarjeta,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            if (prospectosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (filtroFecha.isBlank()) "No se registró nadie aún" else "No hay registros para esta fecha",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Los prospectos que guardes aparecerán aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(prospectosFiltrados) { prospecto ->
                        val isChecked = selectedIds[prospecto.id] == true
                        ProspectoItemCard(
                            prospecto = prospecto,
                            isChecked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedIds[prospecto.id] = true
                                } else {
                                    selectedIds.remove(prospecto.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProspectoItemCard(
    prospecto: ProspectoEntity,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Tarjeta,
        border = BorderStroke(1.dp, TarjetaBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Acento,
                    uncheckedColor = TextoSecundario,
                    checkmarkColor = MaterialTheme.colorScheme.background
                )
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = prospecto.nombre,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Acento.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = prospecto.curso.ifEmpty { "Informática" },
                                color = Acento,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = prospecto.fechaCaptura.substringBefore(" "),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSecundario
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = prospecto.correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${prospecto.telefono} - RUT ${prospecto.rut}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${prospecto.modalidad} · ${prospecto.canalContacto} · ${prospecto.nivelEducativo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
    }
}
