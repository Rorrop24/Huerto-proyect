package cl.duoc.fotomotion.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.fotomotion.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccesoViewModel : ViewModel() {
    val cuentasEjemplo = listOf(
        CuentaHuerto("USR-001", "Profesor Carlos Silva", TipoCuenta.DOCENTE),
        CuentaHuerto("USR-002", "Estudiante Mateo Rojas", TipoCuenta.ALUMNO)
    )
    private val cuentaMutable = MutableStateFlow<CuentaHuerto?>(null)
    val cuentaActiva = cuentaMutable.asStateFlow()

    fun entrarConCuenta(cuentaElegida: CuentaHuerto) { cuentaMutable.value = cuentaElegida }
    fun salirDeCuenta() { cuentaMutable.value = null }
}
