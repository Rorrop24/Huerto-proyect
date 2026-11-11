package cl.duoc.fotomotion.domain.validation

import cl.duoc.fotomotion.domain.model.ParcelaHuerto
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
        return try {
            LocalDate.parse(texto)
            ""
        } catch (problema: DateTimeParseException) {
            "Escribe una fecha real: AAAA-MM-DD."
        }
    }

    fun revisarSector(codigoElegido: Int?, sectores: List<ParcelaHuerto>, mensajeSinSeleccion: String): String = when {
        codigoElegido == null -> mensajeSinSeleccion
        sectores.none { parcela -> parcela.codigoParcela == codigoElegido } -> "El sector ya no está disponible."
        else -> ""
    }
}
