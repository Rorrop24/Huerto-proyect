package cl.duoc.fotomotion.ui.sectores

import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SectoresViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria()
    private val viewModel = SectoresViewModel(Datos.repositorio(dao))

    private fun llenarFormulario(nombre: String = "Invernadero", lugar: String = "Patio norte") {
        viewModel.abrirFormularioNuevo()
        viewModel.escribirNombre(nombre)
        viewModel.escribirUbicacion(lugar)
    }

    @Test
    fun guardarUnSectorValidoLimpiaYCierraElFormulario() = runTest {
        // Arrange
        llenarFormulario()
        viewModel.elegirEstado(EstadoSector.EN_MANTENIMIENTO)
        // Act
        viewModel.guardarSector(Datos.docente)
        // Assert
        val estado = viewModel.uiState.value
        assertEquals(1, estado.sectores.size)
        assertEquals(EstadoSector.EN_MANTENIMIENTO, estado.sectores.first().estado)
        assertEquals("USR-001", estado.sectores.first().responsableId)
        assertEquals("Sector guardado.", estado.aviso)
        assertFalse(estado.formularioVisible)
        assertEquals("", estado.textoNombre)
    }

    @Test
    fun conDatosInvalidosMuestraErroresYNoGuarda() = runTest {
        llenarFormulario(nombre = "", lugar = "ab")
        viewModel.guardarSector(Datos.docente)
        val estado = viewModel.uiState.value
        assertEquals("Escribe el nombre del sector.", estado.errorNombre)
        assertEquals("Usa entre 3 y 80 caracteres.", estado.errorUbicacion)
        assertTrue(dao.sectores.value.isEmpty())
        // Al escribir de nuevo el error desaparece.
        viewModel.escribirNombre("Invernadero")
        assertEquals("", viewModel.uiState.value.errorNombre)
    }

    @Test
    fun unEstudianteNoPuedeCrearSectores() = runTest {
        llenarFormulario()
        viewModel.guardarSector(Datos.mateo)
        assertTrue(dao.sectores.value.isEmpty())
    }

    @Test
    fun editarCargaLosDatosYGuardaLosCambios() = runTest {
        llenarFormulario()
        viewModel.guardarSector(Datos.docente)
        val guardada = viewModel.uiState.value.sectores.first()

        viewModel.editarSector(guardada)
        assertEquals("Invernadero", viewModel.uiState.value.textoNombre)
        assertTrue(viewModel.uiState.value.formularioVisible)
        viewModel.escribirNombre("Invernadero grande")
        viewModel.guardarSector(Datos.docente)

        assertEquals("Invernadero grande", dao.sectores.value.single().nombre)
    }

    @Test
    fun siLaBaseFallaSeConservanLosDatosEscritos() = runTest {
        llenarFormulario()
        dao.fallar = true
        viewModel.guardarSector(Datos.docente)
        val estado = viewModel.uiState.value
        assertEquals("No se pudo guardar el sector.", estado.aviso)
        assertEquals("Invernadero", estado.textoNombre)
        assertTrue(estado.formularioVisible)
        assertFalse(estado.guardando)
    }

    @Test
    fun noSeEliminaUnSectorConCultivos() = runTest {
        llenarFormulario()
        viewModel.guardarSector(Datos.docente)
        val sector = viewModel.uiState.value.sectores.first()
        dao.cultivos.value = listOf(Cultivo(id = 50, sectorId = sector.id, frecuenciaRiegoDias = 2))

        viewModel.eliminarSector(sector)
        assertEquals("Primero elimina los cultivos y tareas de este sector.", viewModel.uiState.value.aviso)

        dao.cultivos.value = emptyList()
        viewModel.editarSector(sector)
        viewModel.eliminarSector(sector)
        assertEquals("Sector eliminado.", viewModel.uiState.value.aviso)
        assertTrue(viewModel.uiState.value.sectores.isEmpty())
        assertNull(viewModel.uiState.value.sectorEnEdicion)
    }

    @Test
    fun siFallaElBorradoSeAvisa() = runTest {
        llenarFormulario()
        viewModel.guardarSector(Datos.docente)
        dao.fallar = true
        viewModel.eliminarSector(viewModel.uiState.value.sectores.first())
        assertEquals("No se pudo eliminar el sector.", viewModel.uiState.value.aviso)
    }

    @Test
    fun cancelarDejaElFormularioVacio() = runTest {
        llenarFormulario()
        viewModel.limpiarFormulario()
        assertFalse(viewModel.uiState.value.formularioVisible)
        assertEquals("", viewModel.uiState.value.textoUbicacion)
    }
}
