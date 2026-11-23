package cl.duoc.fotomotion.domain.model

enum class TipoCuenta(val etiqueta: String) {
    DOCENTE("Docente"),
    ALUMNO("Estudiante");

    companion object {
        fun desde(codigo: String) = entries.find { tipo -> tipo.name == codigo } ?: ALUMNO
    }
}

// Cuenta con la sesión iniciada. No guarda la contraseña.
data class Cuenta(val codigo: String, val nombre: String, val tipo: TipoCuenta) {
    val esDocente: Boolean get() = tipo == TipoCuenta.DOCENTE
}
