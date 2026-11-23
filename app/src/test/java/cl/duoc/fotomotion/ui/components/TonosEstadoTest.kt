package cl.duoc.fotomotion.ui.components

import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.EstadoTarea
import org.junit.Assert.assertEquals
import org.junit.Test

class TonosEstadoTest {
    @Test
    fun cadaEstadoTieneSuColor() {
        assertEquals(TonoEstado.EXITO, EstadoSector.EN_USO.tono())
        assertEquals(TonoEstado.ERROR, EstadoSector.FUERA_DE_USO.tono())
        assertEquals(TonoEstado.ADVERTENCIA, EstadoCultivo.EN_CRECIMIENTO.tono())
        assertEquals(TonoEstado.NEUTRO, EstadoTarea.PENDIENTE.tono())
        assertEquals(TonoEstado.EXITO, EstadoTarea.VALIDADA.tono())
        assertEquals(TonoEstado.ERROR, EstadoTarea.RECHAZADA.tono())
        assertEquals(TonoEstado.ADVERTENCIA, EstadoProblema.EN_REVISION.tono())
    }

    @Test
    fun muestraFechaYHoraSinSegundos() {
        assertEquals("2026-11-11 10:30", mostrarMomento("2026-11-11T10:30:15"))
    }
}
