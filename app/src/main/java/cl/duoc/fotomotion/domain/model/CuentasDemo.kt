package cl.duoc.fotomotion.domain.model

// Cuentas ficticias de demostración. En la base solo se guarda el hash de cada contraseña.
data class CuentaDemo(val id: Int, val nombre: String, val correo: String, val contrasena: String, val rol: TipoCuenta) {
    val codigo: String get() = Usuario.codigoParaId(id)
}

object CuentasDemo {
    val lista = listOf(
        CuentaDemo(1, "Profesor Carlos Silva", "docente@huerto.cl", "Docente2026", TipoCuenta.DOCENTE),
        CuentaDemo(2, "Estudiante Mateo Rojas", "mateo@huerto.cl", "Mateo2026", TipoCuenta.ALUMNO),
        CuentaDemo(3, "Estudiante Sofía Muñoz", "sofia@huerto.cl", "Sofia2026", TipoCuenta.ALUMNO),
        CuentaDemo(4, "Estudiante Diego Pérez", "diego@huerto.cl", "Diego2026", TipoCuenta.ALUMNO)
    )
}
