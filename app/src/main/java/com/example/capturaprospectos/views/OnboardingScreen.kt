package com.example.capturaprospectos.views

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.capturaprospectos.ui.theme.Acento
import com.example.capturaprospectos.ui.theme.NegroDuoc
import com.example.capturaprospectos.ui.theme.Tarjeta
import com.example.capturaprospectos.ui.theme.TextoSecundario
import kotlinx.coroutines.launch

data class OnboardingPage(
    val paso: String,
    val titulo: String,
    val descripcion: String,
    val opcionTexto: String
)

@Composable
fun OnboardingScreen(navController: NavHostController) {
    // Secuencia detallada del funcionamiento real de la app de captura offline
    val pages = listOf(
        OnboardingPage(
            paso = "PASO 1 de 4",
            titulo = "Acceso y Funcionamiento Offline",
            descripcion = "La aplicación opera de manera totalmente local (offline) con base de datos Room. Inicia sesión con tus credenciales corporativas o regístrate.",
            opcionTexto = "Opciones: [ Iniciar Sesión ] o [ Registro de cuenta ]"
        ),
        OnboardingPage(
            paso = "PASO 2 de 4",
            titulo = "Captación de Prospectos en Terreno",
            descripcion = "Permite registrar los datos de personas interesadas en información sobre materias y carreras de forma rápida y estructurada.",
            opcionTexto = "Opción: [ Nuevo Formulario ] (RUT, nombre, carrera, modalidad, canal)"
        ),
        OnboardingPage(
            paso = "PASO 3 de 4",
            titulo = "Lista de Formularios y Filtros",
            descripcion = "Visualiza todos los registros guardados. Cuenta con filtros avanzados por fecha y selección múltiple de elementos.",
            opcionTexto = "Opciones: [ Filtrar por fecha ], [ Selección múltiple ], [ Eliminar ] y [ Exportar a CSV ]"
        ),
        OnboardingPage(
            paso = "PASO 4 de 4",
            titulo = "Gestión de Pendientes y Sincronización",
            descripcion = "Almacena de forma segura los registros que no se han sincronizado debido a la ausencia temporal de internet, listos para enviarse.",
            opcionTexto = "Opción: [ Ver Pendientes ] y reintento de sincronización"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NegroDuoc)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Indicadores de página superiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (isSelected) 24.dp else 8.dp,
                            height = 6.dp
                        )
                        .clip(CircleShape)
                        .background(if (isSelected) Acento else TextoSecundario.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Carrusel horizontal con la secuencia paso a paso
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                // Tarjeta central representativa del módulo
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Tarjeta),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Duoc UC",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = page.paso,
                            color = Acento,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = page.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = page.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Detalle explícito de las opciones de la app
                Text(
                    text = page.opcionTexto,
                    style = MaterialTheme.typography.labelMedium,
                    color = Acento,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Barra inferior con botón de saltar y flecha de avance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            ) {
                Text("Saltar guía", color = Acento, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            navController.navigate("login") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Acento)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Siguiente",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}