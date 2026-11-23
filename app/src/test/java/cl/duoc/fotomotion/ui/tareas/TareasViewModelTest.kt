package cl.duoc.fotomotion.ui.tareas

import cl.duoc.fotomotion.domain.model.EstadoTarea
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoTarea
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class TareasViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria().apply {
        agregarCuentasDemo()
        sectores.value = listOf(Sector(id = 1, nombre = "Invernadero"),
            Sector(id = 2, nombre = "Bancal"))
        cultivos.value = listOf(Cultivo(id = 5, sectorId = 1, frecuenciaRiegoDias = 2))
    }
    private val viewModel = TareasViewModel(Datos.repositorio(dao), Datos.repositorioCuentas(dao))

    private fun crearTareaParaMateo() {
        viewModel.abrirFormularioNuevo()
        viewModel.escribirDescripcion("Regar lechugas")
        viewModel.escribirFecha("2026-12-01")
        viewModel.elegirTipo(TipoTarea.RIEGO)
        viewModel.elegirSector(1)
        viewModel.elegirCultivo(5)
        viewModel.elegirEstudiante(Datos.mateo)
        viewModel.guardarTarea(Datos.docente)
    }

    private fun tareaGuardada() = dao.tareas.value.single()

    @Test
    fun elDocenteCreaUnaTareaAsignadaAUnEstudiante() = runTest {
        crearTareaParaMateo()
        val tarea = tareaGuardada()
        assertEquals("USR-002", tarea.responsableId)
        assertEquals(TipoTarea.RIEGO, tarea.tipo)
        assertEquals(5, tarea.cultivoId)
        assertEquals(EstadoTarea.PENDIENTE, tarea.estado)
        assertEquals("Tarea guardada.", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.formularioVisible)
        // Los estudiantes de la lista vienen de la base de usuarios.
        assertEquals(3, viewModel.uiState.value.estudiantes.size)
    }

    @Test
    fun sinEstudianteAsignadoNoSeGuarda() = runTest {
        viewModel.abrirFormularioNuevo()
        viewModel.escribirDescripcion("Regar lechugas")
        viewModel.elegirSector(1)
        viewModel.guardarTarea(Datos.docente)
        assertEquals("Asigna la tarea a un estudiante.", viewModel.uiState.value.errorEstudiante)
        assertTrue(dao.tareas.value.isEmpty())
        // Un estudiante tampoco puede crear tareas.
        viewModel.elegirEstudiante(Datos.mateo)
        viewModel.guardarTarea(Datos.mateo)
        assertTrue(dao.tareas.value.isEmpty())
    }

    @Test
    fun alCambiarDeSectorSeQuitaElCultivoDeOtroSector() = runTest {
        viewModel.abrirFormularioNuevo()
        viewModel.elegirSector(1)
        viewModel.elegirCultivo(5)
        viewModel.elegirSector(2)
        assertNull(viewModel.uiState.value.cultivoElegido)
    }

    @Test
    fun editarCargaLaTareaConSuEstudiante() = runTest {
        crearTareaParaMateo()
        viewModel.editarTarea(tareaGuardada())
        assertEquals("Regar lechugas", viewModel.uiState.value.textoDescripcion)
        assertEquals("USR-002", viewModel.uiState.value.estudianteAsignado)
        viewModel.elegirEstudiante(Datos.sofia)
        viewModel.guardarTarea(Datos.docente)
        assertEquals("USR-003", tareaGuardada().responsableId)
    }

    @Test
    fun soloElEstudianteAsignadoEntregaLaEvidencia() = runTest {
        crearTareaParaMateo()
        viewModel.iniciarEntrega(tareaGuardada())
        viewModel.adjuntarFoto("content://foto/1")
        viewModel.escribirComentario("Regué todo el sector")

        viewModel.entregarTarea(Datos.sofia)
        assertEquals("Solo el estudiante asignado puede entregar esta tarea.", viewModel.uiState.value.aviso)
        assertEquals(EstadoTarea.PENDIENTE, tareaGuardada().estado)

        viewModel.entregarTarea(Datos.mateo)
        assertEquals(EstadoTarea.PENDIENTE_DE_VALIDACION, tareaGuardada().estado)
        assertEquals("content://foto/1", tareaGuardada().fotografia)
        assertEquals("Evidencia enviada al docente.", viewModel.uiState.value.aviso)
        assertNull(viewModel.uiState.value.tareaEnEntrega)
    }

    @Test
    fun laEntregaNecesitaFotoYComentarioYNoSeRepite() = runTest {
        crearTareaParaMateo()
        viewModel.iniciarEntrega(tareaGuardada())
        viewModel.entregarTarea(Datos.mateo)
        assertEquals("Adjunta una foto con cámara o galería.", viewModel.uiState.value.errorFoto)
        assertEquals("Cuenta qué hiciste en el huerto.", viewModel.uiState.value.errorComentario)

        viewModel.iniciarEntrega(tareaGuardada().copy(estado = EstadoTarea.VALIDADA))
        viewModel.entregarTarea(Datos.mateo)
        assertEquals("Esta tarea ya fue entregada.", viewModel.uiState.value.aviso)
    }

    @Test
    fun elDocenteApruebaORechazaUnaEntrega() = runTest {
        crearTareaParaMateo()
        val entregada = tareaGuardada().copy(estado = EstadoTarea.PENDIENTE_DE_VALIDACION)
        dao.tareas.value = listOf(entregada)

        viewModel.revisarEntrega(entregada, aceptarTrabajo = false, cuentaResponsable = Datos.docente)
        assertEquals(EstadoTarea.RECHAZADA, tareaGuardada().estado)
        assertEquals("Tarea devuelta al estudiante.", viewModel.uiState.value.aviso)

        viewModel.revisarEntrega(entregada, aceptarTrabajo = true, cuentaResponsable = Datos.docente)
        assertEquals(EstadoTarea.VALIDADA, tareaGuardada().estado)
        assertEquals("USR-001", tareaGuardada().validadorId)

        // Un estudiante no puede revisar, y no se revisa una tarea que no está en revisión.
        viewModel.revisarEntrega(tareaGuardada(), aceptarTrabajo = false, cuentaResponsable = Datos.mateo)
        viewModel.revisarEntrega(tareaGuardada(), aceptarTrabajo = false, cuentaResponsable = Datos.docente)
        assertEquals(EstadoTarea.VALIDADA, tareaGuardada().estado)
    }

    @Test
    fun eliminarYFallasDeLaBase() = runTest {
        crearTareaParaMateo()
        viewModel.editarTarea(tareaGuardada())
        viewModel.eliminarTarea(tareaGuardada())
        assertEquals("Tarea eliminada.", viewModel.uiState.value.aviso)
        assertTrue(dao.tareas.value.isEmpty())

        dao.fallar = true
        crearTareaParaMateo()
        assertEquals("No se pudo guardar la tarea.", viewModel.uiState.value.aviso)
        assertEquals("Regar lechugas", viewModel.uiState.value.textoDescripcion)
        viewModel.eliminarTarea(Tarea(id = 1, sectorId = 1))
        assertEquals("No se pudo eliminar la tarea.", viewModel.uiState.value.aviso)
        viewModel.limpiarFormulario()
        assertEquals("", viewModel.uiState.value.textoDescripcion)
    }
}
