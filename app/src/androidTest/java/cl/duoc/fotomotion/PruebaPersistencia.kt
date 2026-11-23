package cl.duoc.fotomotion

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.fotomotion.data.local.BaseDatosHuerto
import cl.duoc.fotomotion.data.repository.RepositorioCuentas
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PruebaPersistencia {
    @Test
    fun conservaDatosAlReabrirYProtegeRelaciones() = runBlocking {
        val contextoPrueba = ApplicationProvider.getApplicationContext<Context>()
        val nombrePrueba = "prueba_huerto_${System.nanoTime()}.db"
        var basePrueba = Room.databaseBuilder(contextoPrueba, BaseDatosHuerto::class.java, nombrePrueba).build()
        try {
            var repositorioPrueba = RepositorioHuerto(basePrueba.huertoDao())
            repositorioPrueba.registrarSector(Sector(nombre = "Sector prueba", ubicacion = "Patio"))
            val sectorGuardado = repositorioPrueba.leerSectores().first().single()
            repositorioPrueba.actualizarSector(sectorGuardado.copy(ubicacion = "Invernadero"))
            repositorioPrueba.registrarCultivo(Cultivo(nombre = "Lechuga",
                sectorId = sectorGuardado.id, frecuenciaRiegoDias = 2))
            assertFalse(repositorioPrueba.eliminarSector(sectorGuardado))
            val cultivoGuardado = repositorioPrueba.leerCultivos().first().single()
            repositorioPrueba.actualizarCultivo(cultivoGuardado.copy(nombre = "Lechuga romana"))
            repositorioPrueba.registrarTarea(Tarea(descripcion = "Regar lechugas",
                sectorId = sectorGuardado.id, cultivoId = cultivoGuardado.id))
            assertFalse(repositorioPrueba.eliminarCultivo(cultivoGuardado))
            val tareaGuardada = repositorioPrueba.leerTareas().first().single()
            repositorioPrueba.actualizarTarea(tareaGuardada.copy(estado = EstadoTarea.VALIDADA))
            basePrueba.close()
            basePrueba = Room.databaseBuilder(contextoPrueba, BaseDatosHuerto::class.java, nombrePrueba).build()
            repositorioPrueba = RepositorioHuerto(basePrueba.huertoDao())
            assertEquals("Invernadero", repositorioPrueba.leerSectores().first().single().ubicacion)
            assertEquals("Lechuga romana", repositorioPrueba.leerCultivos().first().single().nombre)
            assertEquals(EstadoTarea.VALIDADA, repositorioPrueba.leerTareas().first().single().estado)
            repositorioPrueba.eliminarTarea(tareaGuardada)
            assertTrue(repositorioPrueba.eliminarCultivo(cultivoGuardado))
            assertTrue(repositorioPrueba.eliminarSector(sectorGuardado))
            assertTrue(repositorioPrueba.leerSectores().first().isEmpty())
        } finally {
            basePrueba.close()
            contextoPrueba.deleteDatabase(nombrePrueba)
        }
    }

    @Test
    fun registraEstudiantesSinDuplicarYGuardaSoloElHash() = runBlocking {
        val contextoPrueba = ApplicationProvider.getApplicationContext<Context>()
        val basePrueba = Room.inMemoryDatabaseBuilder(contextoPrueba, BaseDatosHuerto::class.java).build()
        try {
            val repositorioPrueba = RepositorioCuentas(basePrueba.huertoDao())
            val cuenta = repositorioPrueba.registrarEstudiante("Ana Pérez", "Ana@Huerto.cl", "Huerto123")
            assertEquals(TipoCuenta.ALUMNO, cuenta?.tipo)
            // El mismo correo no se puede registrar dos veces (se compara sin mayúsculas).
            assertNull(repositorioPrueba.registrarEstudiante("Otra Ana", "ana@huerto.cl", "Huerto456"))
            assertEquals(cuenta, repositorioPrueba.ingresar("ana@huerto.cl", "Huerto123"))
            assertNull(repositorioPrueba.ingresar("ana@huerto.cl", "Incorrecta1"))
            val guardado = basePrueba.huertoDao().buscarUsuarioPorCorreo("ana@huerto.cl")
            assertNotEquals("Huerto123", guardado?.hashContrasena)
            assertEquals(1, repositorioPrueba.leerEstudiantes().first().size)
        } finally {
            basePrueba.close()
        }
    }
}
