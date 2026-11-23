package cl.duoc.fotomotion.domain.validation

data class ErroresIngreso(val correo: String = "", val contrasena: String = "") {
    val esValido: Boolean get() = correo.isEmpty() && contrasena.isEmpty()
}

data class ErroresRegistro(val nombre: String = "", val correo: String = "", val contrasena: String = "", val confirmacion: String = "") {
    val esValido: Boolean get() = listOf(nombre, correo, contrasena, confirmacion).all { error -> error.isEmpty() }
}

// Reglas del inicio de sesión y del registro de estudiantes.
object ValidadorAcceso {
    fun validarIngreso(correo: String, contrasena: String) = ErroresIngreso(
        correo = ValidacionesHuerto.revisarCorreo(correo),
        contrasena = if (contrasena.isEmpty()) "Escribe tu contraseña." else ""
    )

    fun validarRegistro(nombre: String, correo: String, contrasena: String, confirmacion: String) = ErroresRegistro(
        nombre = ValidacionesHuerto.revisarTexto(nombre, 3, 50, "Escribe tu nombre."),
        correo = ValidacionesHuerto.revisarCorreo(correo),
        contrasena = ValidacionesHuerto.revisarContrasenaNueva(contrasena),
        confirmacion = when {
            confirmacion.isEmpty() -> "Repite la contraseña."
            confirmacion != contrasena -> "Las contraseñas no coinciden."
            else -> ""
        }
    )
}
