package cl.duoc.fotomotion.data

import cl.duoc.fotomotion.data.local.ConvertidoresHuerto
import cl.duoc.fotomotion.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

// Room guarda los enum como texto: cada valor debe volver igual al leerlo,
// y un texto desconocido debe usar el valor por defecto en vez de cerrar la app.
class ConvertidoresHuertoTest {
    private val convertidores = ConvertidoresHuerto()

    @Test
    fun cadaEnumSeGuardaYSeLeeIgual() {
        TipoCuenta.entries.forEach { valor -> assertEquals(valor, convertidores.textoATipoCuenta(convertidores.tipoCuentaATexto(valor))) }
        EstadoSector.entries.forEach { valor -> assertEquals(valor, convertidores.textoAEstadoSector(convertidores.estadoSectorATexto(valor))) }
        EstadoCultivo.entries.forEach { valor -> assertEquals(valor, convertidores.textoAEstadoCultivo(convertidores.estadoCultivoATexto(valor))) }
        TipoTarea.entries.forEach { valor -> assertEquals(valor, convertidores.textoATipoTarea(convertidores.tipoTareaATexto(valor))) }
        EstadoTarea.entries.forEach { valor -> assertEquals(valor, convertidores.textoAEstadoTarea(convertidores.estadoTareaATexto(valor))) }
        TipoProblema.entries.forEach { valor -> assertEquals(valor, convertidores.textoATipoProblema(convertidores.tipoProblemaATexto(valor))) }
        EstadoProblema.entries.forEach { valor -> assertEquals(valor, convertidores.textoAEstadoProblema(convertidores.estadoProblemaATexto(valor))) }
    }

    @Test
    fun unTextoDesconocidoUsaElValorPorDefecto() {
        assertEquals("PENDIENTE", convertidores.estadoTareaATexto(EstadoTarea.PENDIENTE))
        assertEquals(TipoCuenta.ALUMNO, convertidores.textoATipoCuenta("ADMIN"))
        assertEquals(EstadoSector.EN_USO, convertidores.textoAEstadoSector(""))
        assertEquals(EstadoCultivo.SEMBRADO, convertidores.textoAEstadoCultivo("CRECIENDO"))
        assertEquals(TipoTarea.OTRA, convertidores.textoATipoTarea("PODA"))
        assertEquals(EstadoTarea.PENDIENTE, convertidores.textoAEstadoTarea("TERMINADA"))
        assertEquals(TipoProblema.OTRO, convertidores.textoATipoProblema("GRANIZO"))
        assertEquals(EstadoProblema.INFORMADO, convertidores.textoAEstadoProblema("ABIERTO"))
    }
}
