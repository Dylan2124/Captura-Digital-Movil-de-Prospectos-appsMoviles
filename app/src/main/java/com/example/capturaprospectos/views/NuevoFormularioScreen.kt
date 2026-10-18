package com.example.capturaprospectos.views

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TarjetaBorde
import com.example.capturaprospectos.ui.theme.TextoSecundario
import com.example.capturaprospectos.utils.CANALES_CONTACTO
import com.example.capturaprospectos.utils.CURSOS
import com.example.capturaprospectos.utils.MODALIDADES
import com.example.capturaprospectos.utils.NIVELES_EDUCATIVOS
import com.example.capturaprospectos.utils.errorCorreo
import com.example.capturaprospectos.utils.errorNombre
import com.example.capturaprospectos.utils.errorRut
import com.example.capturaprospectos.utils.errorTelefono
import com.example.capturaprospectos.utils.normalizarRut
import kotlinx.coroutines.launch

/**
 * Pantallas 3 y 4 del prototipo: NUEVO FORMULARIO + VALIDACIÓN DE DATOS (RF01, RF02, RF03).
 * La validación vive en el mismo composable: cada campo calcula su error con las funciones
 * de utils/Validaciones.kt y lo pinta apenas el usuario lo toca.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoFormularioScreen(navController: NavHostController, appState: AppState) {
    // ---- Estado del formulario (mismo patrón remember/mutableStateOf del docente) ----
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }
    var canal by remember { mutableStateOf("") }
    var errorDuplicado by remember { mutableStateOf<String?>(null) }

    // Campos que el usuario ya tocó: el error solo se muestra después (no al abrir la pantalla)
    val tocados = remember { mutableStateMapOf<String, Boolean>() }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ---- Errores calculados en cada recomposición = validación en tiempo real ----
    val eNombre = errorNombre(nombre)
    val eCorreo = errorCorreo(correo)
    val eRut = errorRut(rut) ?: errorDuplicado
    val eTelefono = errorTelefono(telefono)

    // El botón solo se habilita si TODO es válido y los selectores están completos
    val formularioValido = eNombre == null && eCorreo == null && eRut == null &&
        eTelefono == null && nivel.isNotEmpty() && curso.isNotEmpty() &&
        modalidad.isNotEmpty() && canal.isNotEmpty()

    fun limpiarFormulario() {
        nombre = ""; correo = ""; rut = ""; telefono = ""
        nivel = ""; curso = ""; modalidad = ""; canal = ""
        errorDuplicado = null
        tocados.clear()
    }

    fun guardar() {
        val ok = appState.guardarProspecto(
            rut = normalizarRut(rut), nombre = nombre, correo = correo, telefono = telefono,
            nivel = nivel, curso = curso, modalidad = modalidad, canal = canal
        )
        if (ok) {
            limpiarFormulario() // RF01: el formulario se vacía para el siguiente prospecto
            scope.launch { snackbar.showSnackbar("Prospecto registrado correctamente") }
        } else {
            errorDuplicado = "Ya existe un prospecto con este RUT"
            tocados["rut"] = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Column {
                        Text("Nuevo prospecto", fontWeight = FontWeight.Bold)
                        Text("FORMULARIO DE CAPTACIÓN", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // Recurso nativo: Detecta eventos del taclado para ajusta el diceño
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Etiqueta("NOMBRE COMPLETO")
            CampoTexto(
                valor = nombre,
                onCambio = { nombre = it; tocados["nombre"] = true },
                placeholder = "Nombre y apellido",
                error = if (tocados["nombre"] == true) eNombre else null
            )

            Etiqueta("CORREO ELECTRÓNICO")
            CampoTexto(
                valor = correo,
                onCambio = { correo = it; tocados["correo"] = true },
                placeholder = "correo@ejemplo.com",
                teclado = KeyboardType.Email,
                error = if (tocados["correo"] == true) eCorreo else null
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Etiqueta("RUT")
                    CampoTexto(
                        valor = rut,
                        onCambio = { rut = it; errorDuplicado = null; tocados["rut"] = true },
                        placeholder = "12.345.678-9",
                        error = if (tocados["rut"] == true) eRut else null
                    )
                }
                Column(Modifier.weight(1f)) {
                    Etiqueta("TELÉFONO")
                    CampoTexto(
                        valor = telefono,
                        // Solo dígitos y máximo 9 (RF03: restringir a caracteres numéricos)
                        onCambio = { nuevo ->
                            telefono = nuevo.filter { it.isDigit() }.take(9)
                            tocados["telefono"] = true
                        },
                        placeholder = "9 1234 5678",
                        prefijo = "+56 ",
                        teclado = KeyboardType.Phone,
                        error = if (tocados["telefono"] == true) eTelefono else null
                    )
                }
            }

            Etiqueta("NIVEL EDUCATIVO")
            CampoDesplegable(nivel, NIVELES_EDUCATIVOS, "Seleccionar...") { nivel = it }

            Etiqueta("CURSO / ÁREA DE INTERÉS")
            CampoDesplegable(curso, CURSOS, "Seleccionar del catálogo...") { curso = it }
            if (curso.isNotEmpty()) {
                TextButton(onClick = { curso = "" }) {
                    Text("Limpiar selección", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                }
            }

            Etiqueta("MODALIDAD")
            GrupoOpciones(MODALIDADES, modalidad) { modalidad = it }

            Etiqueta("CANAL DE CONTACTO PREFERIDO")
            GrupoOpciones(CANALES_CONTACTO, canal) { canal = it }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { guardar() },
                enabled = formularioValido,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Acento,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Tarjeta,
                    disabledContentColor = TextoSecundario
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text("Guardar prospecto", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---------------- Componentes reutilizables del formulario ----------------

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.labelSmall,
        color = TextoSecundario,
        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    prefijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(placeholder, color = TextoSecundario) },
        prefix = prefijo?.let { { Text(it) } },
        singleLine = true,
        isError = error != null,                              // contorno en rojo
        supportingText = error?.let { { Text(it) } },         // mensaje bajo el campo
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CampoDesplegable(
    valor: String,
    opciones: List<String>,
    placeholder: String,
    onSeleccion: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = TextoSecundario) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente para que todo el campo abra el menú
        Box(Modifier.matchParentSize().clickable { abierto = true })
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = { onSeleccion(opcion); abierto = false }
                )
            }
        }
    }
}

/** Botones de selección única en filas de 2 (Modalidad y Canal de contacto). */
@Composable
private fun GrupoOpciones(opciones: List<String>, seleccionada: String, onSeleccion: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { opcion ->
                    val activa = opcion == seleccionada
                    Surface(
                        onClick = { onSeleccion(opcion) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (activa) Acento.copy(alpha = 0.15f) else Tarjeta,
                        border = BorderStroke(1.dp, if (activa) Acento else TarjetaBorde),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                            Text(opcion, color = if (activa) Acento else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
