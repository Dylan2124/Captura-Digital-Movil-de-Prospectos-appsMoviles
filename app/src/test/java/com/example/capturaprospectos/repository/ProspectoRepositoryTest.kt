package com.example.capturaprospectos.repository

import com.example.capturaprospectos.data.network.ApiService
import com.example.capturaprospectos.data.network.ProspectoDto
import com.example.capturaprospectos.data.repository.ProspectoRepository
import com.example.capturaprospectos.data.room.ProspectoDao
import com.example.capturaprospectos.data.room.ProspectoEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class ProspectoRepositoryTest {

    @Test
    fun observarProspectosLocales_retornaFlow() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

        val listaMock = listOf(
            ProspectoEntity(1L, "12.345.678-5", "Juan Perez", "juan@example.com", "912345678", "Universitaria", "Ingeniería", "Online", "Redes", "2023-10-01")
        )
        every { prospectoDao.observarTodos() } returns flowOf(listaMock)

        val resultado = repository.observarProspectosLocales().toList()

        assertEquals(1, resultado.size)
        assertEquals("Juan Perez", resultado[0][0].nombre)
        clearAllMocks()
    }

    @Test
    fun obtenerProspectosRemoto_exitoso() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

        val mockDto = ProspectoDto(
            id = 1L,
            rut = "12.345.678-5",
            nombre = "Juan Perez",
            correo = "juan@example.com",
            telefono = "912345678",
            nivelEducativo = "Universitaria",
            curso = "Ingeniería",
            modalidad = "Online",
            canalContacto = "Redes Sociales",
            fechaCaptura = "2023-10-01"
        )

        coEvery { apiService.obtenerProspectos() } returns Response.success(listOf(mockDto))

        val resultado = repository.obtenerProspectosRemotos()

        assertTrue(resultado.isSuccess)
        coVerify(atLeast = 1) { prospectoDao.insertar(any()) }
        clearAllMocks()
    }

    @Test
    fun obtenerProspectosRemoto_falla_retornaFailure() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

        coEvery { apiService.obtenerProspectos() } returns Response.error(500, "".toResponseBody(null))

        val resultado = repository.obtenerProspectosRemotos()

        assertTrue(resultado.isFailure)
        clearAllMocks()
    }

    @Test
    fun crearProspecto_exitoso() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

        val entidad = ProspectoEntity(
            id = 1L,
            rut = "12.345.678-5",
            nombre = "Juan Perez",
            correo = "juan@example.com",
            telefono = "912345678",
            nivelEducativo = "Universitaria",
            curso = "Ingeniería",
            modalidad = "Online",
            canalContacto = "Redes Sociales",
            fechaCaptura = "2023-10-01"
        )

        val mockDto = ProspectoDto(
            id = 1L,
            rut = entidad.rut,
            nombre = entidad.nombre,
            correo = entidad.correo,
            telefono = entidad.telefono,
            nivelEducativo = entidad.nivelEducativo,
            curso = entidad.curso,
            modalidad = entidad.modalidad,
            canalContacto = entidad.canalContacto,
            fechaCaptura = entidad.fechaCaptura
        )

        coEvery { apiService.crearProspecto(any()) } returns Response.success(mockDto)

        val resultado = repository.crearProspecto(entidad)

        assertTrue(resultado.isSuccess)
        coVerify(atLeast = 1) { prospectoDao.insertar(any()) }
        clearAllMocks()
    }

    @Test
    fun crearProspecto_duplicadoRut_manejaError400() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

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

        val errorBody = "Ya existe un prospecto con el RUT".toResponseBody(null)
        coEvery { apiService.crearProspecto(any()) } returns Response.error(400, errorBody)

        val resultado = repository.crearProspecto(entidad)

        assertTrue(resultado.isSuccess) // Maneja el 400 de duplicado guardándolo localmente como sincronizado
        clearAllMocks()
    }

    @Test
    fun actualizarProspecto_exitoso() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

        val entidad = ProspectoEntity(
            id = 1L,
            rut = "12.345.678-5",
            nombre = "Juan Perez",
            correo = "juan@example.com",
            telefono = "912345678",
            nivelEducativo = "Universitaria",
            curso = "Ingeniería",
            modalidad = "Online",
            canalContacto = "Redes Sociales",
            fechaCaptura = "2023-10-01"
        )

        val mockDto = ProspectoDto(
            id = 1L,
            rut = entidad.rut,
            nombre = entidad.nombre,
            correo = entidad.correo,
            telefono = entidad.telefono,
            nivelEducativo = entidad.nivelEducativo,
            curso = entidad.curso,
            modalidad = entidad.modalidad,
            canalContacto = entidad.canalContacto,
            fechaCaptura = entidad.fechaCaptura
        )

        coEvery { apiService.actualizarProspecto(any(), any()) } returns Response.success(mockDto)

        val resultado = repository.actualizarProspecto(1L, entidad)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) { prospectoDao.actualizar(any()) }
        clearAllMocks()
    }

    @Test
    fun eliminarProspecto_conId_exitoso() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

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
            fechaCaptura = "2023-10-01",
            estado = "SINCRONIZADO"
        )

        coEvery { apiService.eliminarProspecto(1L) } returns Response.success(Unit)

        val resultado = repository.eliminarProspecto(entidad)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) { apiService.eliminarProspecto(1L) }
        coVerify(exactly = 1) { prospectoDao.eliminar(entidad) }

        clearAllMocks()
    }

    @Test
    fun eliminarProspecto_ignora404() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

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
            fechaCaptura = "2023-10-01",
            estado = "SINCRONIZADO"
        )

        coEvery { apiService.eliminarProspecto(1L) } returns Response.error(404, "".toResponseBody(null))

        val resultado = repository.eliminarProspecto(entidad)

        assertTrue(resultado.isSuccess) // 404 en eliminación remota se tolera y borra localmente
        coVerify(exactly = 1) { prospectoDao.eliminar(entidad) }

        clearAllMocks()
    }

    @Test
    fun guardarSoloLocal() = runTest {
        val apiService = mockk<ApiService>()
        val prospectoDao = mockk<ProspectoDao>(relaxed = true)
        val repository = ProspectoRepository(apiService, prospectoDao)

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

        coEvery { prospectoDao.insertar(any()) } returns 1L

        val idInsertado = repository.guardarSoloLocal(entidad)

        assertEquals(1L, idInsertado)
        coVerify(exactly = 1) { prospectoDao.insertar(any()) }

        clearAllMocks()
    }
}
