package cl.duoc.fotomotion.domain

import cl.duoc.fotomotion.domain.model.Alerta
import cl.duoc.fotomotion.domain.model.TipoAlerta
import cl.duoc.fotomotion.ui.acceso.AccesoUiState
import cl.duoc.fotomotion.ui.acceso.ModoAcceso
import cl.duoc.fotomotion.ui.alertas.AlertasUiState
import cl.duoc.fotomotion.ui.tareas.TareasUiState
import cl.duoc.fotomotion.ui.sectores.SectoresUiState
import cl.duoc.fotomotion.ui.cultivos.CultivosUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Los estados iniciales deben partir vacíos: así un registro nuevo no muestra datos anteriores.
class UiStateTest {
    @Test
    fun losFormulariosPartenCerradosYVacios() {
        val sectores = SectoresUiState()
        assertFalse(sectores.formularioVisible)
        assertEquals("", sectores.textoNombre)
        assertNull(sectores.sectorEnEdicion)

        val cultivos = CultivosUiState()
        assertFalse(cultivos.formularioVisible)
        assertEquals("1", cultivos.textoRiego)

        val tareas = TareasUiState()
        assertFalse(tareas.formularioVisible)
        assertNull(tareas.estudianteAsignado)
        assertNull(tareas.tareaEnEntrega)
    }

    @Test
    fun elAccesoParteSinSesion() {
        val acceso = AccesoUiState()
        assertEquals(ModoAcceso.INGRESAR, acceso.modo)
        assertNull(acceso.cuentaActiva)
        assertTrue(acceso.textoContrasena.isEmpty())
    }

    @Test
    fun lasAlertasCuentanSusElementos() {
        assertEquals(0, AlertasUiState().cantidad)
        assertEquals(2, AlertasUiState(listOf(Alerta(TipoAlerta.TAREA_ATRASADA, "Regar", ""),
            Alerta(TipoAlerta.PROXIMO_RIEGO, "Lechuga", ""))).cantidad)
    }
}
