package cl.duoc.fotomotion.domain.alertas

import cl.duoc.fotomotion.domain.model.*
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Calcula los recordatorios a partir de los registros. Son solo informativos: no activan dispositivos.
object GeneradorAlertas {
    private const val DIAS_AVISO_RIEGO = 2L
    private const val DIAS_AVISO_COSECHA = 14L
    private const val DIAS_AVISO_MANTENIMIENTO = 7L

    fun generar(hoy: LocalDate, labores: List<LaborHuerto>, siembras: List<SiembraHuerto>,
        problemas: List<ProblemaHuerto>): List<AlertaHuerto> {
        val alertas = mutableListOf<AlertaHuerto>()

        labores.forEach { labor ->
            val fechaProgramada = ValidacionesHuerto.leerFecha(labor.diaPlanificado)
            when {
                labor.estaAtrasada(hoy) -> alertas.add(AlertaHuerto(TipoAlerta.TAREA_ATRASADA,
                    labor.detalleLabor, "Estaba programada para el ${labor.diaPlanificado}."))
                labor.situacionLabor == "PENDIENTE_DE_VALIDACION" -> alertas.add(AlertaHuerto(TipoAlerta.VALIDACION_PENDIENTE,
                    labor.detalleLabor, "El estudiante envió evidencia y espera revisión."))
                labor.claseLabor == TipoLabor.MANTENIMIENTO.name && labor.situacionLabor == "PENDIENTE" &&
                    fechaProgramada != null && !fechaProgramada.isAfter(hoy.plusDays(DIAS_AVISO_MANTENIMIENTO)) ->
                    alertas.add(AlertaHuerto(TipoAlerta.MANTENIMIENTO_PROGRAMADO, labor.detalleLabor,
                        "Programado para el ${labor.diaPlanificado}."))
                else -> Unit
            }
        }

        siembras.filter { siembra -> EstadoCultivo.desde(siembra.situacionSiembra).activo }.forEach { siembra ->
            val diaRiego = proximoRiego(siembra, hoy)
            if (diaRiego != null && !diaRiego.isAfter(hoy.plusDays(DIAS_AVISO_RIEGO))) {
                alertas += AlertaHuerto(TipoAlerta.PROXIMO_RIEGO, siembra.tituloSiembra, describirRiego(diaRiego, hoy))
            }
            val diaCosecha = ValidacionesHuerto.leerFecha(siembra.diaCosecha)
            if (diaCosecha != null && !diaCosecha.isAfter(hoy.plusDays(DIAS_AVISO_COSECHA))) {
                alertas += AlertaHuerto(TipoAlerta.PROXIMA_COSECHA, siembra.tituloSiembra,
                    if (diaCosecha.isBefore(hoy)) "La cosecha estimada era el $diaCosecha." else "Cosecha estimada para el $diaCosecha.")
            }
        }

        problemas.filter { problema -> !EstadoProblema.desde(problema.situacionProblema).resuelto }.forEach { problema ->
            alertas += AlertaHuerto(TipoAlerta.PROBLEMA_PENDIENTE,
                TipoProblema.desde(problema.claseProblema).etiqueta, problema.detalleProblema)
        }
        return alertas.sortedBy { alerta -> alerta.tipo.ordinal }
    }

    // Siguiente día de riego contando desde la siembra según la frecuencia; null si faltan datos.
    fun proximoRiego(siembra: SiembraHuerto, hoy: LocalDate): LocalDate? {
        val inicio = ValidacionesHuerto.leerFecha(siembra.diaSiembra) ?: return null
        if (siembra.intervaloRiego <= 0) return null
        if (!inicio.isBefore(hoy)) return inicio
        val diasDesdeUltimoRiego = ChronoUnit.DAYS.between(inicio, hoy) % siembra.intervaloRiego
        return if (diasDesdeUltimoRiego == 0L) hoy else hoy.plusDays(siembra.intervaloRiego - diasDesdeUltimoRiego)
    }

    private fun describirRiego(diaRiego: LocalDate, hoy: LocalDate) = when (ChronoUnit.DAYS.between(hoy, diaRiego)) {
        0L -> "Toca regar hoy."
        1L -> "Toca regar mañana."
        else -> "Toca regar el $diaRiego."
    }
}
