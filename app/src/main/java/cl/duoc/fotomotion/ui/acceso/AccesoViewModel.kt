package cl.duoc.fotomotion.ui.acceso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioCuentas
import cl.duoc.fotomotion.domain.model.CuentaDemo
import cl.duoc.fotomotion.domain.model.CuentasDemo
import cl.duoc.fotomotion.domain.validation.ValidadorAcceso
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Recibe el repositorio desde afuera: en la app lo entrega FabricaViewModels y en las pruebas uno con datos en memoria.
class AccesoViewModel(private val repositorioCuentas: RepositorioCuentas) : ViewModel() {
    private val _uiState = MutableStateFlow(AccesoUiState())
    val uiState: StateFlow<AccesoUiState> = _uiState.asStateFlow()

    val cuentasDemo: List<CuentaDemo> = CuentasDemo.lista

    fun cambiarModo(modoNuevo: ModoAcceso) {
        // Al cambiar entre ingresar y registrarse el formulario parte vacío.
        _uiState.value = AccesoUiState(modo = modoNuevo)
    }
    fun escribirNombre(texto: String) { _uiState.value = _uiState.value.copy(textoNombre = texto, errorNombre = "", aviso = "") }
    fun escribirCorreo(texto: String) { _uiState.value = _uiState.value.copy(textoCorreo = texto, errorCorreo = "", aviso = "") }
    fun escribirContrasena(texto: String) { _uiState.value = _uiState.value.copy(textoContrasena = texto, errorContrasena = "", aviso = "") }
    fun escribirConfirmacion(texto: String) { _uiState.value = _uiState.value.copy(textoConfirmacion = texto, errorConfirmacion = "") }

    // Completa el formulario con una cuenta de demostración para la presentación.
    fun usarCuentaDemo(cuenta: CuentaDemo) {
        _uiState.value = AccesoUiState(textoCorreo = cuenta.correo, textoContrasena = cuenta.contrasena)
    }

    fun ingresar() {
        val formulario = _uiState.value
        if (formulario.procesando) return
        val errores = ValidadorAcceso.validarIngreso(formulario.textoCorreo, formulario.textoContrasena)
        _uiState.value = formulario.copy(errorCorreo = errores.correo, errorContrasena = errores.contrasena)
        if (!errores.esValido) return
        _uiState.value = _uiState.value.copy(procesando = true)
        viewModelScope.launch {
            try {
                val cuenta = repositorioCuentas.ingresar(formulario.textoCorreo, formulario.textoContrasena)
                _uiState.value = if (cuenta != null) AccesoUiState(cuentaActiva = cuenta)
                    else _uiState.value.copy(textoContrasena = "", aviso = "Correo o contraseña incorrectos.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                _uiState.value = _uiState.value.copy(aviso = "No se pudo iniciar sesión. Intenta nuevamente.")
            } finally { _uiState.value = _uiState.value.copy(procesando = false) }
        }
    }

    fun registrarse() {
        val formulario = _uiState.value
        if (formulario.procesando) return
        val errores = ValidadorAcceso.validarRegistro(formulario.textoNombre, formulario.textoCorreo,
            formulario.textoContrasena, formulario.textoConfirmacion)
        _uiState.value = formulario.copy(errorNombre = errores.nombre, errorCorreo = errores.correo,
            errorContrasena = errores.contrasena, errorConfirmacion = errores.confirmacion)
        if (!errores.esValido) return
        _uiState.value = _uiState.value.copy(procesando = true)
        viewModelScope.launch {
            try {
                val cuenta = repositorioCuentas.registrarEstudiante(formulario.textoNombre, formulario.textoCorreo, formulario.textoContrasena)
                // Si falla, se conservan los datos escritos para que el usuario los corrija.
                _uiState.value = if (cuenta != null) AccesoUiState(cuentaActiva = cuenta)
                    else _uiState.value.copy(errorCorreo = "Ese correo ya está registrado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                _uiState.value = _uiState.value.copy(aviso = "No se pudo crear la cuenta. Intenta nuevamente.")
            } finally { _uiState.value = _uiState.value.copy(procesando = false) }
        }
    }

    fun salirDeCuenta() { _uiState.value = AccesoUiState() }
}
