package cl.duoc.fotomotion.prueba

import cl.duoc.fotomotion.data.repository.RepositorioCuentas
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.TipoCuenta
import kotlinx.coroutines.Dispatchers

// Cuentas y repositorio que comparten las pruebas.
object Datos {
    val docente = Cuenta("USR-001", "Profesor Carlos Silva", TipoCuenta.DOCENTE)
    val mateo = Cuenta("USR-002", "Estudiante Mateo Rojas", TipoCuenta.ALUMNO)
    val sofia = Cuenta("USR-003", "Estudiante Sofía Muñoz", TipoCuenta.ALUMNO)

    fun repositorio(dao: HuertoDaoEnMemoria) = RepositorioHuerto(dao)

    // El cifrado corre en el mismo hilo para que las pruebas no tengan que esperar.
    fun repositorioCuentas(dao: HuertoDaoEnMemoria) = RepositorioCuentas(dao, Dispatchers.Unconfined)
}
