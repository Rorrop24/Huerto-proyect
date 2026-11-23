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

    fun generar(hoy: LocalDate, tareas: List<Tarea>, cultivos: List<Cultivo>,
        problemas: List<Problema>): List<Alerta> {
        val alertas = mutableListOf<Alerta>()

        tareas.forEach { tarea ->
            val fechaProgramada = ValidacionesHuerto.leerFecha(tarea.fechaProgramada)
            when {
                tarea.estaAtrasada(hoy) -> alertas.add(Alerta(TipoAlerta.TAREA_ATRASADA,
                    tarea.descripcion, "Estaba programada para el ${tarea.fechaProgramada}."))
                tarea.estado == EstadoTarea.PENDIENTE_DE_VALIDACION -> alertas.add(Alerta(TipoAlerta.VALIDACION_PENDIENTE,
                    tarea.descripcion, "El estudiante envió evidencia y espera revisión."))
                tarea.tipo == TipoTarea.MANTENIMIENTO && tarea.estado == EstadoTarea.PENDIENTE &&
                    fechaProgramada != null && !fechaProgramada.isAfter(hoy.plusDays(DIAS_AVISO_MANTENIMIENTO)) ->
                    alertas.add(Alerta(TipoAlerta.MANTENIMIENTO_PROGRAMADO, tarea.descripcion,
                        "Programado para el ${tarea.fechaProgramada}."))
                else -> Unit
            }
        }

        cultivos.filter { cultivo -> cultivo.estado.activo }.forEach { cultivo ->
            val diaRiego = proximoRiego(cultivo, hoy)
            if (diaRiego != null && !diaRiego.isAfter(hoy.plusDays(DIAS_AVISO_RIEGO))) {
                alertas += Alerta(TipoAlerta.PROXIMO_RIEGO, cultivo.nombre, describirRiego(diaRiego, hoy))
            }
            val fechaCosecha = ValidacionesHuerto.leerFecha(cultivo.fechaCosecha)
            if (fechaCosecha != null && !fechaCosecha.isAfter(hoy.plusDays(DIAS_AVISO_COSECHA))) {
                alertas += Alerta(TipoAlerta.PROXIMA_COSECHA, cultivo.nombre,
                    if (fechaCosecha.isBefore(hoy)) "La cosecha estimada era el $fechaCosecha." else "Cosecha estimada para el $fechaCosecha.")
            }
        }

        problemas.filter { problema -> !problema.estado.resuelto }.forEach { problema ->
            alertas += Alerta(TipoAlerta.PROBLEMA_PENDIENTE,
                problema.tipo.etiqueta, problema.descripcion)
        }
        return alertas.sortedBy { alerta -> alerta.tipo.ordinal }
    }

    // Siguiente día de riego contando desde la siembra según la frecuencia; null si faltan datos.
    fun proximoRiego(cultivo: Cultivo, hoy: LocalDate): LocalDate? {
        val inicio = ValidacionesHuerto.leerFecha(cultivo.fechaSiembra) ?: return null
        if (cultivo.frecuenciaRiegoDias <= 0) return null
        if (!inicio.isBefore(hoy)) return inicio
        val diasDesdeUltimoRiego = ChronoUnit.DAYS.between(inicio, hoy) % cultivo.frecuenciaRiegoDias
        return if (diasDesdeUltimoRiego == 0L) hoy else hoy.plusDays(cultivo.frecuenciaRiegoDias - diasDesdeUltimoRiego)
    }

    private fun describirRiego(diaRiego: LocalDate, hoy: LocalDate) = when (ChronoUnit.DAYS.between(hoy, diaRiego)) {
        0L -> "Toca regar hoy."
        1L -> "Toca regar mañana."
        else -> "Toca regar el $diaRiego."
    }
}
