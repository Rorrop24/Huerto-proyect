package cl.duoc.fotomotion.domain.validation

import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.model.admiteAsignaciones
import java.time.LocalDate
import java.time.format.DateTimeParseException

// Reglas de los formularios. Cada función devuelve el mensaje de error o "" si el dato es válido.
object ValidacionesHuerto {
    fun revisarTexto(texto: String, minimo: Int, maximo: Int, mensajeVacio: String): String = when {
        texto.isBlank() -> mensajeVacio
        texto.trim().length < minimo || texto.length > maximo -> "Usa entre $minimo y $maximo caracteres."
        else -> ""
    }

    fun revisarFecha(texto: String): String {
        if (texto.isBlank()) return "Escribe una fecha."
        if (texto.length != 10) return "Usa el formato AAAA-MM-DD."
        return if (leerFecha(texto) == null) "Escribe una fecha real: AAAA-MM-DD." else ""
    }

    fun revisarFechaOpcional(texto: String): String = if (texto.isBlank()) "" else revisarFecha(texto)

    // Solo compara cuando ambas fechas son válidas; los formatos se revisan aparte.
    fun revisarOrden(fechaInicial: String, fechaFinal: String, mensaje: String): String {
        val inicio = leerFecha(fechaInicial) ?: return ""
        val fin = leerFecha(fechaFinal) ?: return ""
        return if (fin.isBefore(inicio)) mensaje else ""
    }

    fun revisarNumero(texto: String, minimo: Int, maximo: Int, mensajeVacio: String): String {
        if (texto.isBlank()) return mensajeVacio
        val numero = texto.trim().toIntOrNull() ?: return "Escribe solo números."
        return if (numero < minimo || numero > maximo) "Usa un número entre $minimo y $maximo." else ""
    }

    fun revisarSector(codigoElegido: Int?, sectores: List<ParcelaHuerto>, mensajeSinSeleccion: String): String = when {
        codigoElegido == null -> mensajeSinSeleccion
        sectores.none { parcela -> parcela.codigoParcela == codigoElegido && parcela.admiteAsignaciones } -> "El sector ya no está disponible."
        else -> ""
    }

    // El cultivo es opcional, pero si se elige debe existir y pertenecer al sector.
    fun revisarCultivo(codigoCultivo: Int?, codigoSector: Int?, cultivos: List<SiembraHuerto>): String {
        if (codigoCultivo == null) return ""
        val cultivo = cultivos.find { siembra -> siembra.codigoSiembra == codigoCultivo } ?: return "El cultivo ya no está disponible."
        return if (cultivo.parcelaAsociada != codigoSector) "El cultivo no pertenece al sector elegido." else ""
    }

    // Devuelve la fecha si el texto tiene el formato AAAA-MM-DD y es real; si no, null.
    fun leerFecha(texto: String?): LocalDate? {
        if (texto == null || texto.length != 10) return null
        return try { LocalDate.parse(texto) } catch (problema: DateTimeParseException) { null }
    }
}
