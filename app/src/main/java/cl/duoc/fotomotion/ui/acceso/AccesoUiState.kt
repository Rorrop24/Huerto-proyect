package cl.duoc.fotomotion.ui.acceso

import cl.duoc.fotomotion.domain.model.Cuenta

enum class ModoAcceso { INGRESAR, REGISTRARSE }

data class AccesoUiState(
    val modo: ModoAcceso = ModoAcceso.INGRESAR,
    val textoNombre: String = "",
    val textoCorreo: String = "",
    val textoContrasena: String = "",
    val textoConfirmacion: String = "",
    val errorNombre: String = "",
    val errorCorreo: String = "",
    val errorContrasena: String = "",
    val errorConfirmacion: String = "",
    val aviso: String = "",
    val procesando: Boolean = false,
    val cuentaActiva: Cuenta? = null
)
