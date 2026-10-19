package com.example.capturaprospectos.views

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.data.room.ProspectoEntity
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TarjetaBorde
import com.example.capturaprospectos.ui.theme.TextoSecundario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaFormulariosScreen(navController: NavHostController, appState: AppState) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val prospectos = appState.prospectos

    // Variables para el filtro de fecha
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }

    val selectedIds = remember { mutableStateMapOf<Long, Boolean>() }

    val focusMes = remember { FocusRequester() }
    val focusAnio = remember { FocusRequester() }


    var prospectoSeleccionado by remember { mutableStateOf<ProspectoEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filtroFecha = remember(dia, mes, anio) {
        listOf(dia, mes, anio).filter { it.isNotEmpty() }.joinToString("-")
    }

    val prospectosFiltrados = remember(prospectos, filtroFecha) {
        if (filtroFecha.isBlank()) {
            prospectos
        } else {
            prospectos.filter { it.fechaCaptura.contains(filtroFecha) }
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
                    IconButton(onClick = { exportarProspectosACSV(context, appState) }) {
                        Icon(Icons.Default.Download, contentDescription = "Exportar CSV", tint = Acento)
                    }
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
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                val idsToDelete = selectedIds.filter { it.value }.keys.toList()
                                appState.eliminarProspectos(idsToDelete)
                                selectedIds.clear()

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
            Text(
                text = "FILTRAR POR FECHA DE CAPTURA",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dia,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        if (soloNumeros.length <= 2) {
                            dia = soloNumeros
                            if (dia.length == 2) focusMes.requestFocus()
                        }
                    },
                    placeholder = { Text("DD", color = TextoSecundario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusMes.requestFocus() }),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Acento, unfocusedBorderColor = TarjetaBorde,
                        focusedContainerColor = Tarjeta, unfocusedContainerColor = Tarjeta,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(text = " - ", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, color = TextoSecundario)

                OutlinedTextField(
                    value = mes,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        if (soloNumeros.length <= 2) {
                            mes = soloNumeros
                            if (mes.length == 2) focusAnio.requestFocus()
                        }
                    },
                    placeholder = { Text("MM", color = TextoSecundario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusAnio.requestFocus() }),
                    modifier = Modifier.weight(1f).focusRequester(focusMes),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Acento, unfocusedBorderColor = TarjetaBorde,
                        focusedContainerColor = Tarjeta, unfocusedContainerColor = Tarjeta,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(text = " - ", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, color = TextoSecundario)

                OutlinedTextField(
                    value = anio,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        if (soloNumeros.length <= 4) {
                            anio = soloNumeros
                        }
                    },
                    placeholder = { Text("AAAA", color = TextoSecundario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.weight(1.5f).focusRequester(focusAnio),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Acento, unfocusedBorderColor = TarjetaBorde,
                        focusedContainerColor = Tarjeta, unfocusedContainerColor = Tarjeta,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            if (prospectosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
                            },
                            onClick = { prospectoSeleccionado = prospecto }
                        )
                    }
                }
            }
        }
    }


    if (prospectoSeleccionado != null) {
        ModalBottomSheet(
            onDismissRequest = { prospectoSeleccionado = null },
            sheetState = sheetState,
            containerColor = Tarjeta
        ) {
            DetalleProspectoPanel(prospecto = prospectoSeleccionado!!)
        }
    }
}



@Composable
private fun DetalleProspectoPanel(prospecto: ProspectoEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 24.dp), // Espacio extra para navegación
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column {
            Text(
                text = prospecto.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Acento.copy(alpha = 0.15f)
            ) {
                Text(
                    text = prospecto.estado,
                    color = Acento,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(color = TarjetaBorde)


        FilaDetalle("RUT", prospecto.rut)
        FilaDetalle("Correo Electrónico", prospecto.correo)
        FilaDetalle("Teléfono", "+56 ${prospecto.telefono}")

        HorizontalDivider(color = TarjetaBorde)

        FilaDetalle("Curso o Área", prospecto.curso)
        FilaDetalle("Nivel Educativo", prospecto.nivelEducativo)
        FilaDetalle("Preferencias", "${prospecto.modalidad} · ${prospecto.canalContacto}")

        HorizontalDivider(color = TarjetaBorde)

        FilaDetalle("Fecha de Captura", prospecto.fechaCaptura)
        FilaDetalle("Captador", prospecto.emailCaptador)
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String) {
    Column {
        Text(text = etiqueta, style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun exportarProspectosACSV(context: Context, appState: AppState) {
    val prospectos = appState.prospectos
    if (prospectos.isEmpty()) {
        Toast.makeText(context, "No hay prospectos para exportar", Toast.LENGTH_SHORT).show()
        return
    }

    val fechaFormateada = SimpleDateFormat("MM_dd_yy", Locale.getDefault()).format(Date())
    val nombreArchivo = "registro_${fechaFormateada}.csv"

    try {
        val contenido = StringBuilder()
        contenido.append("ID;RUT;Nombre;Correo;Telefono;NivelEducativo;Curso;Modalidad;Canal;Fecha;Estado;Captador\n")

        for (p in prospectos) {
            contenido.append("${p.id};${p.rut};\"${p.nombre}\";${p.correo};${p.telefono};${p.nivelEducativo};\"${p.curso}\";${p.modalidad};${p.canalContacto};${p.fechaCaptura};${p.estado};${p.emailCaptador}\n")
        }

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(contenido.toString().toByteArray())
            }
            Toast.makeText(context, "Archivo '$nombreArchivo' guardado en Descargas", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(context, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun ProspectoItemCard(
    prospecto: ProspectoEntity,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Tarjeta,
        border = BorderStroke(1.dp, TarjetaBorde),
        // Hace que toda la tarjeta sea cliqueable para abrir los detalles
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
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
                    Text(
                        text = prospecto.nombre,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = prospecto.fechaCaptura.substringBefore(" "),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSecundario
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Acento.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = prospecto.curso.ifEmpty { "Sin curso" },
                            color = Acento,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "RUT: ${prospecto.rut}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}