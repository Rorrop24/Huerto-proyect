package cl.duoc.fotomotion.domain

import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidacionesHuertoTest {
    @Test
    fun rechazaFechasInexistentesYAceptaOpcionalesVacias() {
        assertEquals("Escribe una fecha real: AAAA-MM-DD.", ValidacionesHuerto.revisarFecha("2026-02-30"))
        assertEquals("", ValidacionesHuerto.revisarFecha("2026-02-28"))
        assertEquals("", ValidacionesHuerto.revisarFechaOpcional(""))
        assertEquals("Usa el formato AAAA-MM-DD.", ValidacionesHuerto.revisarFechaOpcional("2026-2-1"))
    }

    @Test
    fun revisaQueLaCosechaNoSeaAntesDeLaSiembra() {
        assertEquals("antes", ValidacionesHuerto.revisarOrden("2026-05-10", "2026-05-01", "antes"))
        assertEquals("", ValidacionesHuerto.revisarOrden("2026-05-10", "2026-05-10", "antes"))
        assertEquals("", ValidacionesHuerto.revisarOrden("fecha mala", "2026-05-01", "antes"))
    }

    @Test
    fun revisaLaFrecuenciaDeRiego() {
        assertEquals("vacío", ValidacionesHuerto.revisarNumero("", 1, 30, "vacío"))
        assertEquals("Escribe solo números.", ValidacionesHuerto.revisarNumero("dos", 1, 30, "vacío"))
        assertEquals("Usa un número entre 1 y 30.", ValidacionesHuerto.revisarNumero("0", 1, 30, "vacío"))
        assertEquals("", ValidacionesHuerto.revisarNumero("7", 1, 30, "vacío"))
    }

    @Test
    fun noPermiteAsignarSectoresFueraDeUso() {
        val sectores = listOf(
            ParcelaHuerto(codigoParcela = 1, situacionParcela = EstadoSector.EN_USO.name),
            ParcelaHuerto(codigoParcela = 2, situacionParcela = EstadoSector.FUERA_DE_USO.name)
        )
        assertEquals("", ValidacionesHuerto.revisarSector(1, sectores, "elige"))
        assertEquals("El sector ya no está disponible.", ValidacionesHuerto.revisarSector(2, sectores, "elige"))
        assertEquals("elige", ValidacionesHuerto.revisarSector(null, sectores, "elige"))
    }

    @Test
    fun elCultivoDebePertenecerAlSector() {
        val cultivos = listOf(SiembraHuerto(codigoSiembra = 5, parcelaAsociada = 1, intervaloRiego = 2))
        assertEquals("", ValidacionesHuerto.revisarCultivo(null, 1, cultivos))
        assertEquals("", ValidacionesHuerto.revisarCultivo(5, 1, cultivos))
        assertEquals("El cultivo no pertenece al sector elegido.", ValidacionesHuerto.revisarCultivo(5, 2, cultivos))
        assertEquals("El cultivo ya no está disponible.", ValidacionesHuerto.revisarCultivo(9, 1, cultivos))
    }
}
