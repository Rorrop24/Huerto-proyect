package cl.duoc.fotomotion.domain

import cl.duoc.fotomotion.domain.alertas.GeneradorAlertas
import cl.duoc.fotomotion.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GeneradorAlertasTest {
    private val hoy = LocalDate.of(2026, 11, 11)

    @Test
    fun marcaComoAtrasadasSoloLasTareasSinTerminar() {
        val tareas = listOf(
            Tarea(descripcion = "Regar", sectorId = 1, fechaProgramada = "2026-11-01", estado = EstadoTarea.PENDIENTE),
            Tarea(descripcion = "Limpiar", sectorId = 1, fechaProgramada = "2026-11-01", estado = EstadoTarea.VALIDADA),
            Tarea(descripcion = "Cosechar", sectorId = 1, fechaProgramada = "2026-11-20", estado = EstadoTarea.PENDIENTE)
        )
        val alertas = GeneradorAlertas.generar(hoy, tareas, emptyList(), emptyList())
        assertEquals(listOf(TipoAlerta.TAREA_ATRASADA), alertas.map { alerta -> alerta.tipo })
        assertEquals("Regar", alertas.single().titulo)
    }

    @Test
    fun calculaElProximoRiegoSegunLaFrecuencia() {
        val cadaTresDias = Cultivo(sectorId = 1, fechaSiembra = "2026-11-01", frecuenciaRiegoDias = 3)
        val cadaCincoDias = Cultivo(sectorId = 1, fechaSiembra = "2026-11-01", frecuenciaRiegoDias = 5)
        assertEquals(LocalDate.of(2026, 11, 13), GeneradorAlertas.proximoRiego(cadaTresDias, hoy))
        assertEquals(hoy, GeneradorAlertas.proximoRiego(cadaCincoDias, hoy))
        assertEquals(null, GeneradorAlertas.proximoRiego(cadaTresDias.copy(fechaSiembra = null), hoy))
    }

    @Test
    fun avisaCosechasCercanasSoloDeCultivosActivos() {
        val activo = Cultivo(nombre = "Lechuga", sectorId = 1, fechaCosecha = "2026-11-20",
            frecuenciaRiegoDias = 30, fechaSiembra = "2026-10-01", estado = EstadoCultivo.EN_CRECIMIENTO)
        val cosechado = activo.copy(nombre = "Tomate", estado = EstadoCultivo.COSECHADO)
        val alertas = GeneradorAlertas.generar(hoy, emptyList(), listOf(activo, cosechado), emptyList())
        assertEquals(listOf("Lechuga"), alertas.filter { alerta -> alerta.tipo == TipoAlerta.PROXIMA_COSECHA }.map { alerta -> alerta.titulo })
    }

    @Test
    fun avisaProblemasNoResueltosPrimeroLosUrgentes() {
        val problemas = listOf(
            Problema(sectorId = 1, tipo = TipoProblema.PLAGA_SIMULADA,
                descripcion = "Hojas con manchas", estado = EstadoProblema.INFORMADO),
            Problema(sectorId = 1, tipo = TipoProblema.FALTA_AGUA,
                descripcion = "Tierra seca", estado = EstadoProblema.CERRADO)
        )
        val tareas = listOf(Tarea(descripcion = "Revisar", sectorId = 1, estado = EstadoTarea.PENDIENTE_DE_VALIDACION))
        val alertas = GeneradorAlertas.generar(hoy, tareas, emptyList(), problemas)
        assertEquals(listOf(TipoAlerta.PROBLEMA_PENDIENTE, TipoAlerta.VALIDACION_PENDIENTE), alertas.map { alerta -> alerta.tipo })
        assertTrue(alertas.first().tipo.urgente)
    }
}
