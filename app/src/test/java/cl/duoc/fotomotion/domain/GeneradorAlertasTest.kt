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
        val labores = listOf(
            LaborHuerto(detalleLabor = "Regar", parcelaTrabajo = 1, diaPlanificado = "2026-11-01", situacionLabor = "PENDIENTE"),
            LaborHuerto(detalleLabor = "Limpiar", parcelaTrabajo = 1, diaPlanificado = "2026-11-01", situacionLabor = "VALIDADA"),
            LaborHuerto(detalleLabor = "Cosechar", parcelaTrabajo = 1, diaPlanificado = "2026-11-20", situacionLabor = "PENDIENTE")
        )
        val alertas = GeneradorAlertas.generar(hoy, labores, emptyList(), emptyList())
        assertEquals(listOf(TipoAlerta.TAREA_ATRASADA), alertas.map { alerta -> alerta.tipo })
        assertEquals("Regar", alertas.single().titulo)
    }

    @Test
    fun calculaElProximoRiegoSegunLaFrecuencia() {
        val cadaTresDias = SiembraHuerto(parcelaAsociada = 1, diaSiembra = "2026-11-01", intervaloRiego = 3)
        val cadaCincoDias = SiembraHuerto(parcelaAsociada = 1, diaSiembra = "2026-11-01", intervaloRiego = 5)
        assertEquals(LocalDate.of(2026, 11, 13), GeneradorAlertas.proximoRiego(cadaTresDias, hoy))
        assertEquals(hoy, GeneradorAlertas.proximoRiego(cadaCincoDias, hoy))
        assertEquals(null, GeneradorAlertas.proximoRiego(cadaTresDias.copy(diaSiembra = null), hoy))
    }

    @Test
    fun avisaCosechasCercanasSoloDeCultivosActivos() {
        val activo = SiembraHuerto(tituloSiembra = "Lechuga", parcelaAsociada = 1, diaCosecha = "2026-11-20",
            intervaloRiego = 30, diaSiembra = "2026-10-01", situacionSiembra = EstadoCultivo.EN_CRECIMIENTO.name)
        val cosechado = activo.copy(tituloSiembra = "Tomate", situacionSiembra = EstadoCultivo.COSECHADO.name)
        val alertas = GeneradorAlertas.generar(hoy, emptyList(), listOf(activo, cosechado), emptyList())
        assertEquals(listOf("Lechuga"), alertas.filter { alerta -> alerta.tipo == TipoAlerta.PROXIMA_COSECHA }.map { alerta -> alerta.titulo })
    }

    @Test
    fun avisaProblemasNoResueltosPrimeroLosUrgentes() {
        val problemas = listOf(
            ProblemaHuerto(parcelaAfectada = 1, claseProblema = TipoProblema.PLAGA_SIMULADA.name,
                detalleProblema = "Hojas con manchas", situacionProblema = EstadoProblema.INFORMADO.name),
            ProblemaHuerto(parcelaAfectada = 1, claseProblema = TipoProblema.FALTA_AGUA.name,
                detalleProblema = "Tierra seca", situacionProblema = EstadoProblema.CERRADO.name)
        )
        val labores = listOf(LaborHuerto(detalleLabor = "Revisar", parcelaTrabajo = 1, situacionLabor = "PENDIENTE_DE_VALIDACION"))
        val alertas = GeneradorAlertas.generar(hoy, labores, emptyList(), problemas)
        assertEquals(listOf(TipoAlerta.PROBLEMA_PENDIENTE, TipoAlerta.VALIDACION_PENDIENTE), alertas.map { alerta -> alerta.tipo })
        assertTrue(alertas.first().tipo.urgente)
    }
}
