package cl.duoc.fotomotion

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.fotomotion.model.*
import cl.duoc.fotomotion.repository.RepositorioHuerto
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
        var basePrueba = Room.databaseBuilder(contextoPrueba, BaseHuerto::class.java, nombrePrueba).build()
        try {
            var repositorioPrueba = RepositorioHuerto(basePrueba.consultasHuerto())
            repositorioPrueba.registrarParcela(ParcelaHuerto(tituloParcela = "Sector prueba", lugarParcela = "Patio"))
            val parcelaGuardada = repositorioPrueba.leerParcelas().first().single()
            repositorioPrueba.corregirParcela(parcelaGuardada.copy(lugarParcela = "Invernadero"))
            repositorioPrueba.registrarSiembra(SiembraHuerto(tituloSiembra = "Lechuga",
                parcelaAsociada = parcelaGuardada.codigoParcela, intervaloRiego = 2))
            assertFalse(repositorioPrueba.borrarParcela(parcelaGuardada))
            val siembraGuardada = repositorioPrueba.leerSiembras().first().single()
            repositorioPrueba.corregirSiembra(siembraGuardada.copy(tituloSiembra = "Lechuga romana"))
            repositorioPrueba.registrarLabor(LaborHuerto(detalleLabor = "Regar lechugas",
                parcelaTrabajo = parcelaGuardada.codigoParcela, siembraTrabajo = siembraGuardada.codigoSiembra))
            assertFalse(repositorioPrueba.borrarSiembra(siembraGuardada))
            val laborGuardada = repositorioPrueba.leerLabores().first().single()
            repositorioPrueba.corregirLabor(laborGuardada.copy(situacionLabor = "VALIDADA"))
            basePrueba.close()
            basePrueba = Room.databaseBuilder(contextoPrueba, BaseHuerto::class.java, nombrePrueba).build()
            repositorioPrueba = RepositorioHuerto(basePrueba.consultasHuerto())
            assertEquals("Invernadero", repositorioPrueba.leerParcelas().first().single().lugarParcela)
            assertEquals("Lechuga romana", repositorioPrueba.leerSiembras().first().single().tituloSiembra)
            assertEquals("VALIDADA", repositorioPrueba.leerLabores().first().single().situacionLabor)
            repositorioPrueba.borrarLabor(laborGuardada)
            assertTrue(repositorioPrueba.borrarSiembra(siembraGuardada))
            assertTrue(repositorioPrueba.borrarParcela(parcelaGuardada))
            assertTrue(repositorioPrueba.leerParcelas().first().isEmpty())
        } finally {
            basePrueba.close()
            contextoPrueba.deleteDatabase(nombrePrueba)
        }
    }
}
