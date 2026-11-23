package cl.duoc.fotomotion.ui.problemas

import cl.duoc.fotomotion.domain.model.Problema
import kotlinx.coroutines.CompletableDeferred
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ProblemasViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria().apply {
        sectores.value = listOf(Sector(id = 1), Sector(id = 2))
        cultivos.value = listOf(Cultivo(id = 5, sectorId = 1, frecuenciaRiegoDias = 2))
    }
    private val viewModel = ProblemasViewModel(Datos.repositorio(dao))

    private fun reportarFaltaDeAgua() {
        viewModel.abrirReporte()
        viewModel.elegirSector(1)
        viewModel.elegirCultivo(5)
        viewModel.elegirTipo(TipoProblema.FALTA_AGUA)
        viewModel.escribirDescripcion("Tierra muy seca")
        viewModel.adjuntarFoto("content://foto/2")
        viewModel.reportarProblema(Datos.mateo)
    }

    @Test
    fun unReporteValidoQuedaInformado() = runTest {
        reportarFaltaDeAgua()
        val reporte = dao.problemas.value.single()
        assertEquals(EstadoProblema.INFORMADO, reporte.estado)
        assertEquals("USR-002", reporte.autorId)
        assertEquals("content://foto/2", reporte.fotografia)
        assertEquals("Problema reportado. El docente lo revisará.", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.formularioVisible)
    }

    @Test
    fun unReporteIncompletoMuestraErrores() = runTest {
        viewModel.abrirReporte()
        viewModel.reportarProblema(Datos.mateo)
        val estado = viewModel.uiState.value
        assertEquals("Selecciona el sector afectado.", estado.errorSector)
        assertEquals("Elige el tipo de problema.", estado.errorTipo)
        assertEquals("Describe brevemente el problema.", estado.errorDescripcion)
        // Al cambiar de sector se quita el cultivo que no le pertenece.
        viewModel.elegirSector(1)
        viewModel.elegirCultivo(5)
        viewModel.elegirSector(2)
        assertNull(viewModel.uiState.value.cultivoElegido)
    }

    @Test
    fun elDocenteDaSeguimientoYSeGuardaLaFechaDeCierre() = runTest {
        reportarFaltaDeAgua()
        viewModel.iniciarSeguimiento(dao.problemas.value.single())
        viewModel.elegirEstadoSeguimiento(EstadoProblema.ATENDIDO)
        viewModel.escribirSeguimiento("ok")
        viewModel.guardarSeguimiento(Datos.docente)
        assertEquals("Usa entre 3 y 200 caracteres.", viewModel.uiState.value.errorSeguimiento)

        viewModel.escribirSeguimiento("Se regó el sector")
        viewModel.guardarSeguimiento(Datos.mateo) // un estudiante no da seguimiento
        assertEquals(EstadoProblema.INFORMADO, dao.problemas.value.single().estado)

        viewModel.guardarSeguimiento(Datos.docente)
        val atendido = dao.problemas.value.single()
        assertEquals(EstadoProblema.ATENDIDO, atendido.estado)
        assertNotNull(atendido.fechaCierre)
        assertEquals("Seguimiento guardado.", viewModel.uiState.value.aviso)

        viewModel.iniciarSeguimiento(atendido)
        viewModel.elegirEstadoSeguimiento(EstadoProblema.EN_REVISION)
        viewModel.escribirSeguimiento("")
        viewModel.guardarSeguimiento(Datos.docente)
        assertNull(dao.problemas.value.single().fechaCierre)
        viewModel.cancelarSeguimiento()
        assertNull(viewModel.uiState.value.problemaEnSeguimiento)
    }

    @Test
    fun siLaBaseFallaSeAvisa() = runTest {
        dao.fallar = true
        reportarFaltaDeAgua()
        assertEquals("No se pudo reportar el problema.", viewModel.uiState.value.aviso)
        assertEquals("Tierra muy seca", viewModel.uiState.value.textoDescripcion)
        dao.fallar = false
        viewModel.reportarProblema(Datos.mateo)
        dao.fallar = true
        viewModel.iniciarSeguimiento(dao.problemas.value.single())
        viewModel.guardarSeguimiento(Datos.docente)
        assertEquals("No se pudo guardar el seguimiento.", viewModel.uiState.value.aviso)
        viewModel.informar("Aviso")
        assertEquals("Aviso", viewModel.uiState.value.aviso)
    }

    @Test
    fun tocarReportarDosVecesNoDuplicaElProblema() = runTest {
        dao.pausa = CompletableDeferred()
        reportarFaltaDeAgua()
        viewModel.reportarProblema(Datos.mateo)
        dao.pausa?.complete(Unit)
        assertEquals(1, dao.problemas.value.size)
    }

    @Test
    fun unReporteSinFotoYElCultivoDelMismoSectorSeMantiene() = runTest {
        viewModel.abrirReporte()
        viewModel.elegirSector(1)
        viewModel.elegirCultivo(5)
        viewModel.elegirSector(1) // mismo sector: el cultivo sigue elegido
        assertEquals(5, viewModel.uiState.value.cultivoElegido)
        viewModel.elegirTipo(TipoProblema.PLAGA_SIMULADA)
        viewModel.escribirDescripcion("Hojas con manchas")
        viewModel.reportarProblema(Datos.sofia)
        assertNull(dao.problemas.value.single().fotografia)
    }

    @Test
    fun elSeguimientoNecesitaUnProblemaElegidoYNoSeGuardaDosVeces() = runTest {
        viewModel.guardarSeguimiento(Datos.docente) // sin problema elegido no pasa nada
        assertEquals("", viewModel.uiState.value.aviso)

        val cerrado = Problema(id = 8, sectorId = 1, tipo = TipoProblema.OTRO, descripcion = "Reja rota",
            estado = EstadoProblema.ATENDIDO, fechaCierre = "2026-11-01T10:00:00")
        dao.problemas.value = listOf(cerrado)
        viewModel.iniciarSeguimiento(cerrado)
        viewModel.elegirEstadoSeguimiento(EstadoProblema.CERRADO)
        viewModel.escribirSeguimiento("Reja reparada")
        dao.pausa = CompletableDeferred()
        viewModel.guardarSeguimiento(Datos.docente)
        viewModel.guardarSeguimiento(Datos.docente)
        dao.pausa?.complete(Unit)
        // Si ya tenía fecha de cierre, se conserva la original.
        assertEquals("2026-11-01T10:00:00", dao.problemas.value.single().fechaCierre)
        assertEquals(EstadoProblema.CERRADO, dao.problemas.value.single().estado)
    }

    @Test
    fun siLaOperacionSeCancelaNoSeMuestraError() = runTest {
        dao.cancelar = true
        reportarFaltaDeAgua()
        assertEquals("", viewModel.uiState.value.aviso)
        dao.problemas.value = listOf(Problema(id = 8, sectorId = 1, tipo = TipoProblema.OTRO, descripcion = "Reja rota"))
        viewModel.iniciarSeguimiento(dao.problemas.value.single())
        viewModel.escribirSeguimiento("Revisado")
        viewModel.guardarSeguimiento(Datos.docente)
        assertEquals("", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.guardando)
    }
}
