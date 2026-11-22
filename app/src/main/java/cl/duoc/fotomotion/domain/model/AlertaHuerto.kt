package cl.duoc.fotomotion.domain.model

// El orden de las constantes define el orden en que se muestran los recordatorios.
enum class TipoAlerta(val etiqueta: String, val urgente: Boolean) {
    TAREA_ATRASADA("Tarea atrasada", true),
    PROBLEMA_PENDIENTE("Problema sin resolver", true),
    VALIDACION_PENDIENTE("Tarea por validar", false),
    PROXIMO_RIEGO("Próximo riego", false),
    PROXIMA_COSECHA("Próxima cosecha", false),
    MANTENIMIENTO_PROGRAMADO("Mantenimiento programado", false)
}

data class AlertaHuerto(val tipo: TipoAlerta, val titulo: String, val detalle: String)
