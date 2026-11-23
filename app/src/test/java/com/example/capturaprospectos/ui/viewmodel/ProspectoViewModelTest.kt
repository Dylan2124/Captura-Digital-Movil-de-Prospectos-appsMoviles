package com.example.capturaprospectos.ui.viewmodel

import com.example.capturaprospectos.data.repository.ProspectoRepository
import com.example.capturaprospectos.data.room.ProspectoEntity
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProspectoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun limpiarEstado_estableceUiStateEnReposo() = runTest {
        val repository = mockk<ProspectoRepository>()
        every { repository.observarProspectosLocales() } returns flowOf(emptyList())

        val viewModel = ProspectoViewModel(repository)
        viewModel.limpiarEstado()

        assertEquals(ProspectoUiState.Reposo, viewModel.uiState.value)
    }

    @Test
    fun sincronizarDesdeServidor_exitoso() = runTest {
        val repository = mockk<ProspectoRepository>()
        every { repository.observarProspectosLocales() } returns flowOf(emptyList())
        coEvery { repository.obtenerProspectosRemotos() } returns Result.success(emptyList())

        val viewModel = ProspectoViewModel(repository)
        viewModel.sincronizarDesdeServidor()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ProspectoUiState.Exito("Prospectos actualizados"), viewModel.uiState.value)
    }

    @Test
    fun guardarProspecto_nuevo_exitoso() = runTest {
        val repository = mockk<ProspectoRepository>()
        every { repository.observarProspectosLocales() } returns flowOf(emptyList())

        val entidad = ProspectoEntity(
            id = 0L,
            rut = "12.345.678-5",
            nombre = "Juan Perez",
            correo = "juan@example.com",
            telefono = "912345678",
            nivelEducativo = "Universitaria",
            curso = "Ingeniería",
            modalidad = "Online",
            canalContacto = "Redes",
            fechaCaptura = "2023-10-01"
        )
        coEvery { repository.crearProspecto(any()) } returns Result.success(entidad)

        val viewModel = ProspectoViewModel(repository)
        viewModel.guardarProspecto(entidad)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ProspectoUiState.Exito("Prospecto guardado"), viewModel.uiState.value)
    }

    @Test
    fun eliminarProspecto_exitoso() = runTest {
        val repository = mockk<ProspectoRepository>()
        every { repository.observarProspectosLocales() } returns flowOf(emptyList())

        val entidad = ProspectoEntity(
            id = 1L,
            rut = "12.345.678-5",
            nombre = "Juan Perez",
            correo = "juan@example.com",
            telefono = "912345678",
            nivelEducativo = "Universitaria",
            curso = "Ingeniería",
            modalidad = "Online",
            canalContacto = "Redes",
            fechaCaptura = "2023-10-01"
        )
        coEvery { repository.eliminarProspecto(any()) } returns Result.success(Unit)

        val viewModel = ProspectoViewModel(repository)
        viewModel.eliminarProspecto(entidad)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ProspectoUiState.Exito("Prospecto eliminado"), viewModel.uiState.value)
    }
}
