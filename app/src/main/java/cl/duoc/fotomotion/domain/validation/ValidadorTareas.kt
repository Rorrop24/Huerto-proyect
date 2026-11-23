package cl.duoc.fotomotion.domain.validation

import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo

data class ErroresTarea(
    val detalle: String = "",
    val fecha: String = "",
    val sector: String = "",
    val cultivo: String = "",
    val estudiante: String = ""
) {
    val esValido: Boolean get() = listOf(detalle, fecha, sector, cultivo, estudiante).all { error -> error.isEmpty() }
}

data class ErroresEntrega(val foto: String = "", val comentario: String = "") {
    val esValido: Boolean get() = foto.isEmpty() && comentario.isEmpty()
}

// Reglas de creación de tareas (docente) y de entrega de evidencia (estudiante).
object ValidadorTareas {
    fun validarTarea(detalle: String, fecha: String, sectorElegido: Int?, sectores: List<Sector>,
        cultivoElegido: Int?, cultivos: List<Cultivo>, estudianteElegido: String?, estudiantes: List<Cuenta>) = ErroresTarea(
        detalle = ValidacionesHuerto.revisarTexto(detalle, 5, 150, "Explica qué hay que hacer."),
        fecha = ValidacionesHuerto.revisarFecha(fecha),
        sector = ValidacionesHuerto.revisarSector(sectorElegido, sectores, "Selecciona el sector de la tarea."),
        cultivo = ValidacionesHuerto.revisarCultivo(cultivoElegido, sectorElegido, cultivos),
        estudiante = when {
            estudianteElegido == null -> "Asigna la tarea a un estudiante."
            estudiantes.none { cuenta -> cuenta.codigo == estudianteElegido } -> "El estudiante ya no está disponible."
            else -> ""
        }
    )

    fun validarEntrega(direccionFoto: String, comentario: String) = ErroresEntrega(
        foto = if (direccionFoto.isBlank()) "Adjunta una foto con cámara o galería." else "",
        comentario = ValidacionesHuerto.revisarTexto(comentario, 5, 300, "Cuenta qué hiciste en el huerto.")
    )
}
