package cl.duoc.fotomotion.prueba

import cl.duoc.fotomotion.data.local.HuertoDao
import cl.duoc.fotomotion.domain.model.*
import cl.duoc.fotomotion.domain.seguridad.CifradorContrasenas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// DAO falso: guarda todo en listas en memoria en vez de usar Room.
// Con "fallar = true" cada escritura lanza un error para probar los mensajes de falla.
// Con "cancelar = true" la operación se cancela (por ejemplo, si el usuario sale de la pantalla).
// Con "pausa" la escritura queda esperando hasta completar la pausa, como una base lenta.
class HuertoDaoEnMemoria : HuertoDao {
    val sectores = MutableStateFlow<List<Sector>>(emptyList())
    val cultivos = MutableStateFlow<List<Cultivo>>(emptyList())
    val tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val problemas = MutableStateFlow<List<Problema>>(emptyList())
    val usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    var fallar = false
    var cancelar = false
    var pausa: CompletableDeferred<Unit>? = null
    private var siguienteId = 1

    private suspend fun revisarFalla() {
        pausa?.await()
        if (cancelar) throw CancellationException("Operación cancelada")
        if (fallar) throw IllegalStateException("Falla simulada de la base de datos")
    }

    // Igual que Room: los registros nuevos reciben un id y la lista se ordena del más nuevo al más antiguo.
    override suspend fun agregarSector(sector: Sector) { revisarFalla(); sectores.value = listOf(sector.copy(id = siguienteId++)) + sectores.value }
    override suspend fun actualizarSector(sector: Sector) { revisarFalla(); sectores.value = sectores.value.map { actual -> if (actual.id == sector.id) sector else actual } }
    override suspend fun eliminarSector(sector: Sector) { revisarFalla(); sectores.value = sectores.value.filterNot { actual -> actual.id == sector.id } }
    override fun observarSectores(): Flow<List<Sector>> = sectores

    override suspend fun agregarCultivo(cultivo: Cultivo) { revisarFalla(); cultivos.value = listOf(cultivo.copy(id = siguienteId++)) + cultivos.value }
    override suspend fun actualizarCultivo(cultivo: Cultivo) { revisarFalla(); cultivos.value = cultivos.value.map { actual -> if (actual.id == cultivo.id) cultivo else actual } }
    override suspend fun eliminarCultivo(cultivo: Cultivo) { revisarFalla(); cultivos.value = cultivos.value.filterNot { actual -> actual.id == cultivo.id } }
    override fun observarCultivos(): Flow<List<Cultivo>> = cultivos

    override suspend fun agregarTarea(tarea: Tarea) { revisarFalla(); tareas.value = listOf(tarea.copy(id = siguienteId++)) + tareas.value }
    override suspend fun actualizarTarea(tarea: Tarea) { revisarFalla(); tareas.value = tareas.value.map { actual -> if (actual.id == tarea.id) tarea else actual } }
    override suspend fun eliminarTarea(tarea: Tarea) { revisarFalla(); tareas.value = tareas.value.filterNot { actual -> actual.id == tarea.id } }
    override fun observarTareas(): Flow<List<Tarea>> = tareas

    override suspend fun agregarProblema(problema: Problema) { revisarFalla(); problemas.value = listOf(problema.copy(id = siguienteId++)) + problemas.value }
    override suspend fun actualizarProblema(problema: Problema) { revisarFalla(); problemas.value = problemas.value.map { actual -> if (actual.id == problema.id) problema else actual } }
    override fun observarProblemas(): Flow<List<Problema>> = problemas

    override suspend fun agregarUsuario(usuario: Usuario): Long {
        revisarFalla()
        val id = siguienteId++
        usuarios.value = usuarios.value + usuario.copy(id = id)
        return id.toLong()
    }
    override suspend fun buscarUsuarioPorCorreo(correoBuscado: String): Usuario? {
        revisarFalla()
        return usuarios.value.find { usuario -> usuario.correo == correoBuscado }
    }
    override fun observarUsuariosPorRol(rolBuscado: TipoCuenta): Flow<List<Usuario>> =
        usuarios.map { lista -> lista.filter { usuario -> usuario.rol == rolBuscado }.sortedBy { usuario -> usuario.nombre } }

    override suspend fun contarCultivosDeSector(codigoBuscado: Int) = cultivos.value.count { cultivo -> cultivo.sectorId == codigoBuscado }
    override suspend fun contarTareasDeSector(codigoBuscado: Int) = tareas.value.count { tarea -> tarea.sectorId == codigoBuscado }
    override suspend fun contarTareasDeCultivo(codigoBuscado: Int) = tareas.value.count { tarea -> tarea.cultivoId == codigoBuscado }

    // Agrega las cuentas de demostración con su contraseña cifrada, como hace la base real.
    fun agregarCuentasDemo() {
        usuarios.value = CuentasDemo.lista.map { cuenta ->
            val cifrada = CifradorContrasenas.cifrar(cuenta.contrasena)
            Usuario(cuenta.id, cuenta.nombre, cuenta.correo, cuenta.rol, cifrada.hash, cifrada.sal)
        }
        siguienteId = 100
    }
}
