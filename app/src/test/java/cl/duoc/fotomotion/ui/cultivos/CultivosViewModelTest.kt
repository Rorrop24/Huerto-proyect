package cl.duoc.fotomotion.ui.cultivos

import kotlinx.coroutines.CompletableDeferred
import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CultivosViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria().apply {
        sectores.value = listOf(Sector(id = 1, nombre = "Invernadero"))
    }
    private val viewModel = CultivosViewModel(Datos.repositorio(dao))

    private fun llenarFormulario() {
        viewModel.abrirFormularioNuevo()
        viewModel.escribirNombre("Lechuga")
        viewModel.elegirSector(1)
        viewModel.elegirEstado(EstadoCultivo.EN_CRECIMIENTO)
        viewModel.escribirFechaSiembra("2026-09-01")
        viewModel.escribirFechaTrasplante("")
        viewModel.escribirFechaCosecha("2026-12-01")
        viewModel.escribirRiego("3")
    }

    @Test
    fun guardaElCultivoConSusFechasYRiego() = runTest {
        llenarFormulario()
        viewModel.guardarCultivo(Datos.docente)
        val guardado = dao.cultivos.value.single()
        assertEquals("Lechuga", guardado.nombre)
        assertEquals(EstadoCultivo.EN_CRECIMIENTO, guardado.estado)
        assertNull(guardado.fechaTrasplante)
        assertEquals("2026-12-01", guardado.fechaCosecha)
        assertEquals(3, guardado.frecuenciaRiegoDias)
        assertEquals("Cultivo guardado.", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.formularioVisible)
    }

    @Test
    fun validaFechasYRiego() = runTest {
        llenarFormulario()
        viewModel.escribirFechaCosecha("2026-08-01")
        viewModel.escribirRiego("40")
        viewModel.guardarCultivo(Datos.docente)
        val estado = viewModel.uiState.value
        assertEquals("La cosecha no puede ser antes de la siembra.", estado.errorFechaCosecha)
        assertEquals("Usa un número entre 1 y 30.", estado.errorRiego)
        assertTrue(dao.cultivos.value.isEmpty())
    }

    @Test
    fun soloElDocenteGuardaYEditarCargaLosDatos() = runTest {
        llenarFormulario()
        viewModel.guardarCultivo(Datos.mateo)
        assertTrue(dao.cultivos.value.isEmpty())

        viewModel.guardarCultivo(Datos.docente)
        viewModel.editarCultivo(dao.cultivos.value.single())
        assertEquals("Lechuga", viewModel.uiState.value.textoNombre)
        assertEquals("3", viewModel.uiState.value.textoRiego)
        viewModel.escribirNombre("Lechuga romana")
        viewModel.guardarCultivo(Datos.docente)
        assertEquals("Lechuga romana", dao.cultivos.value.single().nombre)
    }

    @Test
    fun noSeEliminaUnCultivoConTareas() = runTest {
        llenarFormulario()
        viewModel.guardarCultivo(Datos.docente)
        val cultivo = dao.cultivos.value.single()
        dao.tareas.value = listOf(Tarea(id = 9, sectorId = 1, cultivoId = cultivo.id))

        viewModel.eliminarCultivo(cultivo)
        assertEquals("Primero elimina las tareas de este cultivo.", viewModel.uiState.value.aviso)

        dao.tareas.value = emptyList()
        viewModel.editarCultivo(cultivo)
        viewModel.eliminarCultivo(cultivo)
        assertEquals("Cultivo eliminado.", viewModel.uiState.value.aviso)
        assertNull(viewModel.uiState.value.cultivoEnEdicion)
    }

    @Test
    fun siLaBaseFallaSeConservanLosDatos() = runTest {
        llenarFormulario()
        dao.fallar = true
        viewModel.guardarCultivo(Datos.docente)
        assertEquals("No se pudo guardar el cultivo.", viewModel.uiState.value.aviso)
        assertEquals("Lechuga", viewModel.uiState.value.textoNombre)

        viewModel.eliminarCultivo(Cultivo(id = 1, sectorId = 1, frecuenciaRiegoDias = 1))
        assertEquals("No se pudo eliminar el cultivo.", viewModel.uiState.value.aviso)
        viewModel.limpiarFormulario()
        assertEquals("", viewModel.uiState.value.textoNombre)
    }

    @Test
    fun tocarGuardarDosVecesNoDuplicaElCultivo() = runTest {
        llenarFormulario()
        dao.pausa = CompletableDeferred()
        viewModel.guardarCultivo(Datos.docente)
        viewModel.guardarCultivo(Datos.docente)
        dao.pausa?.complete(Unit)
        assertEquals(1, dao.cultivos.value.size)
    }

    @Test
    fun guardaElTrasplanteSoloSiSeEscribe() = runTest {
        llenarFormulario()
        viewModel.escribirFechaTrasplante("2026-09-15")
        viewModel.escribirFechaCosecha(" ")
        viewModel.guardarCultivo(Datos.docente)
        val guardado = dao.cultivos.value.single()
        assertEquals("2026-09-15", guardado.fechaTrasplante)
        assertNull(guardado.fechaCosecha)
    }

    @Test
    fun editarUnCultivoSinFechasDejaLosCamposVacios() = runTest {
        viewModel.editarCultivo(Cultivo(id = 3, nombre = "Papa", sectorId = 1, frecuenciaRiegoDias = 4))
        val estado = viewModel.uiState.value
        assertEquals("", estado.textoFechaSiembra)
        assertEquals("", estado.textoFechaTrasplante)
        assertEquals("", estado.textoFechaCosecha)
        assertEquals("4", estado.textoRiego)
        viewModel.editarCultivo(Cultivo(id = 3, nombre = "Papa", sectorId = 1, fechaSiembra = "2026-09-01",
            fechaTrasplante = "2026-09-10", fechaCosecha = "2026-12-01", frecuenciaRiegoDias = 4))
        assertEquals("2026-09-10", viewModel.uiState.value.textoFechaTrasplante)
    }

    @Test
    fun eliminarOtroCultivoNoBorraElFormularioEnEdicion() = runTest {
        dao.cultivos.value = listOf(Cultivo(id = 3, nombre = "Papa", sectorId = 1, frecuenciaRiegoDias = 4),
            Cultivo(id = 4, nombre = "Haba", sectorId = 1, frecuenciaRiegoDias = 4))
        viewModel.editarCultivo(dao.cultivos.value.first())
        viewModel.eliminarCultivo(dao.cultivos.value.last())
        assertEquals("Papa", viewModel.uiState.value.textoNombre)
        assertEquals("Cultivo eliminado.", viewModel.uiState.value.aviso)
    }

    @Test
    fun siLaOperacionSeCancelaNoSeMuestraError() = runTest {
        llenarFormulario()
        dao.cancelar = true
        viewModel.guardarCultivo(Datos.docente)
        viewModel.eliminarCultivo(Cultivo(id = 1, sectorId = 1, frecuenciaRiegoDias = 1))
        assertEquals("", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.guardando)
    }
}
