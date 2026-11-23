package cl.duoc.fotomotion.data.repository

import cl.duoc.fotomotion.data.local.HuertoDao
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.Cultivo

// Datos del huerto: sectores, cultivos, tareas y problemas.
// Las cuentas de usuario (login y registro) están en RepositorioCuentas.
class RepositorioHuerto(private val dao: HuertoDao) {
    fun leerSectores() = dao.observarSectores()
    suspend fun registrarSector(sector: Sector) = dao.agregarSector(sector)
    suspend fun actualizarSector(sector: Sector) = dao.actualizarSector(sector)
    suspend fun eliminarSector(sector: Sector): Boolean {
        if (dao.contarCultivosDeSector(sector.id) > 0 ||
            dao.contarTareasDeSector(sector.id) > 0) return false
        dao.eliminarSector(sector)
        return true
    }

    fun leerCultivos() = dao.observarCultivos()
    suspend fun registrarCultivo(cultivo: Cultivo) = dao.agregarCultivo(cultivo)
    suspend fun actualizarCultivo(cultivo: Cultivo) = dao.actualizarCultivo(cultivo)
    suspend fun eliminarCultivo(cultivo: Cultivo): Boolean {
        if (dao.contarTareasDeCultivo(cultivo.id) > 0) return false
        dao.eliminarCultivo(cultivo)
        return true
    }

    fun leerTareas() = dao.observarTareas()
    suspend fun registrarTarea(tarea: Tarea) = dao.agregarTarea(tarea)
    suspend fun actualizarTarea(tarea: Tarea) = dao.actualizarTarea(tarea)
    suspend fun eliminarTarea(tarea: Tarea) = dao.eliminarTarea(tarea)

    fun leerProblemas() = dao.observarProblemas()
    suspend fun registrarProblema(problema: Problema) = dao.agregarProblema(problema)
    suspend fun actualizarProblema(problema: Problema) = dao.actualizarProblema(problema)
}
