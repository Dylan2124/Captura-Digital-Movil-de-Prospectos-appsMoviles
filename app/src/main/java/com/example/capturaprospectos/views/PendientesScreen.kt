package com.example.capturaprospectos.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.data.AppState
import com.example.capturaprospectos.data.room.ProspectoEntity
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.ErrorNaranja
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TarjetaBorde
import com.example.capturaprospectos.ui.theme.TextoSecundario
import com.example.capturaprospectos.utils.IndicadorInternet
import com.example.capturaprospectos.utils.verificarConexionInternet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendientesScreen(navController: NavHostController, appState: AppState) {
    val pendientes = appState.pendientes
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun sincronizar() {
        val tieneInternet = verificarConexionInternet(context)
        if (!tieneInternet) {
            scope.launch { snackbar.showSnackbar("Sin conexión a Internet. No es posible sincronizar") }
            return
        }
        appState.sincronizarPendientes(context) { enviados, fallidos ->
            scope.launch {
                snackbar.showSnackbar(
                    when {
                        fallidos > 0 -> "Falló la sincronización. Compruebe su red"
                        enviados == 0 -> "No hay registros pendientes"
                        else -> "$enviados registro(s) sincronizado(s) con éxito"
                    }
                )
            }
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
                        Text("Pendientes de sincronizar", fontWeight = FontWeight.Bold)
                        Text(
                            "${pendientes.size} formulario(s) sin enviar",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoSecundario
                        )
                    }
                },
                actions = { IndicadorInternet(modifier = Modifier.padding(end = 16.dp)) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            if (pendientes.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Todo está sincronizado", color = TextoSecundario)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(pendientes, key = { it.id }) { p -> TarjetaPendiente(p) }
                }
            }

            Button(
                onClick = { sincronizar() },
                enabled = pendientes.isNotEmpty() && !appState.sincronizando,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Acento,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Tarjeta,
                    disabledContentColor = TextoSecundario
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(52.dp)
            ) {
                Text(if (appState.sincronizando) "Sincronizando..." else "Sincronizar ahora", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TarjetaPendiente(p: ProspectoEntity) {
    Surface(
        shape = RoundedCornerShape(14.dp), color = Tarjeta, border = BorderStroke(1.dp, TarjetaBorde), modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(p.nombre, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(50), color = ErrorNaranja.copy(alpha = 0.15f)) {
                    Text("PENDIENTE", color = ErrorNaranja, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            Text(p.correo, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            Text(p.curso, style = MaterialTheme.typography.bodySmall)
            Text("Capturado: ${p.fechaCaptura}", color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
        }
    }
}