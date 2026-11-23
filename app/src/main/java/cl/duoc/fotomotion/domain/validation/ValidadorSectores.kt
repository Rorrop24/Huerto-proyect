package cl.duoc.fotomotion.domain.validation

data class ErroresSector(val nombre: String = "", val ubicacion: String = "") {
    val esValido: Boolean get() = nombre.isEmpty() && ubicacion.isEmpty()
}

// Reglas del formulario de sectores.
object ValidadorSectores {
    fun validar(nombre: String, ubicacion: String) = ErroresSector(
        nombre = ValidacionesHuerto.revisarTexto(nombre, 3, 40, "Escribe el nombre del sector."),
        ubicacion = ValidacionesHuerto.revisarTexto(ubicacion, 3, 80, "Escribe dónde está el sector.")
    )
}
