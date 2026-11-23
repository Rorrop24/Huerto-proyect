package cl.duoc.fotomotion.data

import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RepositorioHuertoTest {
    private val dao = HuertoDaoEnMemoria()
    private val repositorio = Datos.repositorio(dao)

    @Test
    fun protegeLasRelacionesAlBorrar() = runTest {
        repositorio.registrarSector(Sector(nombre = "Invernadero"))
        val sector = repositorio.leerSectores().first().single()
        repositorio.registrarCultivo(Cultivo(sectorId = sector.id, frecuenciaRiegoDias = 2))
        val cultivo = repositorio.leerCultivos().first().single()
        repositorio.registrarTarea(Tarea(sectorId = sector.id, cultivoId = cultivo.id))
        val tarea = repositorio.leerTareas().first().single()

        assertFalse(repositorio.eliminarSector(sector))
        assertFalse(repositorio.eliminarCultivo(cultivo))
        repositorio.actualizarTarea(tarea.copy(descripcion = "Regar"))
        assertEquals("Regar", repositorio.leerTareas().first().single().descripcion)
        repositorio.eliminarTarea(tarea)
        assertTrue(repositorio.eliminarCultivo(cultivo))
        assertTrue(repositorio.eliminarSector(sector))
        repositorio.actualizarSector(sector)
        repositorio.actualizarCultivo(cultivo)
    }

    @Test
    fun guardaYCorrigeProblemas() = runTest {
        repositorio.registrarProblema(Problema(sectorId = 1, descripcion = "Plaga"))
        val problema = repositorio.leerProblemas().first().single()
        repositorio.actualizarProblema(problema.copy(descripcion = "Plaga simulada"))
        assertEquals("Plaga simulada", repositorio.leerProblemas().first().single().descripcion)
    }
}
