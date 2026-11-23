package com.example.capturaprospectos.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.TextoSecundario
import com.example.capturaprospectos.ui.viewmodel.UsuarioUiState
import com.example.capturaprospectos.ui.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavHostController, appState: AppState) {
    val context = LocalContext.current
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModel.factory(context)
    )
    val uiState by usuarioViewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val focusMes = remember { FocusRequester() }
    val focusAnio = remember { FocusRequester() }
    val focusCorreo = remember { FocusRequester() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UsuarioUiState.Registrado -> {
                error = ""
                Toast.makeText(context, state.mensaje, Toast.LENGTH_LONG).show()
                usuarioViewModel.limpiarEstado()
                navController.popBackStack()
            }
            is UsuarioUiState.Error -> {
                error = state.mensaje
            }
            else -> Unit
        }
    }

    val cargando = uiState is UsuarioUiState.Cargando

    fun esFechaValida(fecha: String): Boolean {
        if (fecha.length != 10) return false
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(fecha)
            true
        } catch (e: Exception) {
            false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                title = {
                    Column {
                        Text("Registro de cuenta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("PORTAL DUOC UC", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Nombre y Apellido", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = "" },
                    placeholder = { Text("Nombre") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it; error = "" },
                    placeholder = { Text("Apellido") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Text(text = "Fecha de nacimiento", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

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
                            error = ""
                            if (dia.length == 2) focusMes.requestFocus()
                        }
                    },
                    placeholder = { Text("DD") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusMes.requestFocus() }),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Text(text = " / ", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                OutlinedTextField(
                    value = mes,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        if (soloNumeros.length <= 2) {
                            mes = soloNumeros
                            error = ""
                            if (mes.length == 2) focusAnio.requestFocus()
                        }
                    },
                    placeholder = { Text("MM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusAnio.requestFocus() }),
                    modifier = Modifier.weight(1f).focusRequester(focusMes),
                    singleLine = true
                )

                Text(text = " / ", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                OutlinedTextField(
                    value = anio,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        if (soloNumeros.length <= 4) {
                            anio = soloNumeros
                            error = ""
                            if (anio.length == 4) focusCorreo.requestFocus()
                        }
                    },
                    placeholder = { Text("AAAA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusCorreo.requestFocus() }),
                    modifier = Modifier.weight(1.5f).focusRequester(focusAnio),
                    singleLine = true
                )
            }

            Text(text = "Credenciales", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; error = "" },
                label = { Text("Correo Corporativo") },
                modifier = Modifier.fillMaxWidth().focusRequester(focusCorreo),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = "" },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                enabled = !cargando,
                onClick = {
                    val fechaCompleta = "$dia/$mes/$anio"

                    if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || password.isBlank() || dia.isBlank() || mes.isBlank() || anio.isBlank()) {
                        error = "Todos los campos son obligatorios"
                    } else if (!esFechaValida(fechaCompleta)) {
                        error = "La fecha de nacimiento no es válida. Revisa el formato."
                    } else {
                        error = ""
                        usuarioViewModel.registrar(
                            correo = correo.trim(),
                            password = password,
                            nombre = nombre.trim(),
                            apellido = apellido.trim(),
                            fechaNacimiento = fechaCompleta
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (cargando) "Registrando..." else "Registrarse")
            }

            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Acento)
            }
        }
    }
}