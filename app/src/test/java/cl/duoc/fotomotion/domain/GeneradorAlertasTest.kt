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

    @Test
    fun avisaEntregasPorRevisarYMantenimientosCercanos() {
        val tareas = listOf(
            Tarea(descripcion = "Revisar fotos", sectorId = 1, fechaProgramada = "2026-11-20",
                estado = EstadoTarea.PENDIENTE_DE_VALIDACION),
            Tarea(descripcion = "Arreglar riego", sectorId = 1, fechaProgramada = "2026-11-15",
                tipo = TipoTarea.MANTENIMIENTO),
            Tarea(descripcion = "Mantenimiento lejano", sectorId = 1, fechaProgramada = "2026-12-30",
                tipo = TipoTarea.MANTENIMIENTO)
        )
        val alertas = GeneradorAlertas.generar(hoy, tareas, emptyList(), emptyList())
        assertEquals(listOf("Revisar fotos", "Arreglar riego"), alertas.map { alerta -> alerta.titulo })
        assertEquals("Programado para el 2026-11-15.", alertas.last().detalle)
    }

    @Test
    fun describeCuandoTocaRegar() {
        fun avisoDeRiego(siembra: String, frecuencia: Int) = GeneradorAlertas.generar(hoy, emptyList(),
            listOf(Cultivo(nombre = "Lechuga", sectorId = 1, fechaSiembra = siembra, frecuenciaRiegoDias = frecuencia)),
            emptyList()).single { alerta -> alerta.tipo == TipoAlerta.PROXIMO_RIEGO }.detalle
        assertEquals("Toca regar hoy.", avisoDeRiego("2026-11-01", 5))
        assertEquals("Toca regar mañana.", avisoDeRiego("2026-11-01", 11))
        assertEquals("Toca regar el 2026-11-13.", avisoDeRiego("2026-11-13", 3))
    }

    @Test
    fun sinFechaOFrecuenciaNoHayRiegoYLaCosechaVencidaSeAvisa() {
        assertEquals(null, GeneradorAlertas.proximoRiego(Cultivo(sectorId = 1, fechaSiembra = null, frecuenciaRiegoDias = 3), hoy))
        assertEquals(null, GeneradorAlertas.proximoRiego(Cultivo(sectorId = 1, fechaSiembra = "2026-11-01", frecuenciaRiegoDias = 0), hoy))
        val cosechaAtrasada = Cultivo(nombre = "Rabanito", sectorId = 1, fechaCosecha = "2026-11-05", frecuenciaRiegoDias = 0)
        val alerta = GeneradorAlertas.generar(hoy, emptyList(), listOf(cosechaAtrasada), emptyList()).single()
        assertEquals(TipoAlerta.PROXIMA_COSECHA, alerta.tipo)
        assertEquals("La cosecha estimada era el 2026-11-05.", alerta.detalle)
    }

    @Test
    fun noAvisaMantenimientosYaEntregadosSinFechaOLejanos() {
        val tareas = listOf(
            Tarea(descripcion = "Revisado", sectorId = 1, fechaProgramada = "2026-11-15",
                tipo = TipoTarea.MANTENIMIENTO, estado = EstadoTarea.VALIDADA),
            Tarea(descripcion = "Rechazado", sectorId = 1, fechaProgramada = "2026-11-15",
                tipo = TipoTarea.MANTENIMIENTO, estado = EstadoTarea.RECHAZADA),
            Tarea(descripcion = "Sin fecha", sectorId = 1, fechaProgramada = "", tipo = TipoTarea.MANTENIMIENTO),
            Tarea(descripcion = "Riego", sectorId = 1, fechaProgramada = "2026-11-15", tipo = TipoTarea.RIEGO)
        )
        val cosechaLejana = Cultivo(nombre = "Zapallo", sectorId = 1, fechaCosecha = "2027-03-01", frecuenciaRiegoDias = 0)
        assertTrue(GeneradorAlertas.generar(hoy, tareas, listOf(cosechaLejana), emptyList()).isEmpty())
    }
}
