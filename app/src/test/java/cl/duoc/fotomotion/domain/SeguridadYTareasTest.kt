package cl.duoc.fotomotion.domain

import cl.duoc.fotomotion.data.local.ConvertidoresHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.CuentasDemo
import cl.duoc.fotomotion.domain.model.EstadoTarea
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.TipoTarea
import cl.duoc.fotomotion.domain.model.estaAtrasada
import cl.duoc.fotomotion.domain.model.visiblesPara
import cl.duoc.fotomotion.domain.seguridad.CifradorContrasenas
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadYTareasTest {
    private val docente = Cuenta("USR-001", "Profesor", TipoCuenta.DOCENTE)
    private val mateo = Cuenta("USR-002", "Mateo", TipoCuenta.ALUMNO)
    private val sofia = Cuenta("USR-003", "Sofía", TipoCuenta.ALUMNO)
    private val tareas = listOf(
        Tarea(descripcion = "Regar", sectorId = 1, responsableId = "USR-002"),
        Tarea(descripcion = "Cosechar", sectorId = 1, responsableId = "USR-003")
    )

    @Test
    fun laContrasenaNoSeGuardaEnTextoPlanoYSeVerifica() {
        val cifrada = CifradorContrasenas.cifrar("Mateo2026")
        assertNotEquals("Mateo2026", cifrada.hash)
        assertTrue(CifradorContrasenas.verificar("Mateo2026", cifrada.hash, cifrada.sal))
        assertFalse(CifradorContrasenas.verificar("Mateo2027", cifrada.hash, cifrada.sal))
        // La misma contraseña cifrada dos veces da hashes distintos por la sal aleatoria.
        assertNotEquals(cifrada.hash, CifradorContrasenas.cifrar("Mateo2026").hash)
    }

    @Test
    fun hayUnDocenteYTresEstudiantesDeDemostracion() {
        assertEquals(1, CuentasDemo.lista.count { cuenta -> cuenta.rol == TipoCuenta.DOCENTE })
        assertEquals(3, CuentasDemo.lista.count { cuenta -> cuenta.rol == TipoCuenta.ALUMNO })
        assertEquals(4, CuentasDemo.lista.map { cuenta -> cuenta.correo }.distinct().size)
        assertEquals("USR-001", CuentasDemo.lista.first().codigo)
    }

    @Test
    fun cadaEstudianteVeSoloSusTareas() {
        assertEquals(2, tareas.visiblesPara(docente).size)
        assertEquals(listOf("Regar"), tareas.visiblesPara(mateo).map { tarea -> tarea.descripcion })
        assertEquals(listOf("Cosechar"), tareas.visiblesPara(sofia).map { tarea -> tarea.descripcion })
        assertTrue(tareas.visiblesPara(null).isEmpty())
    }

    @Test
    fun soloSeEntreganTareasPendientesORechazadas() {
        assertTrue(EstadoTarea.PENDIENTE.admiteEntrega)
        assertTrue(EstadoTarea.RECHAZADA.admiteEntrega)
        assertFalse(EstadoTarea.PENDIENTE_DE_VALIDACION.admiteEntrega)
        assertFalse(EstadoTarea.VALIDADA.admiteEntrega)
        val vencida = Tarea(sectorId = 1, fechaProgramada = "2026-11-01")
        assertTrue(vencida.estaAtrasada(LocalDate.of(2026, 11, 11)))
        assertFalse(vencida.copy(estado = EstadoTarea.VALIDADA).estaAtrasada(LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun losConvertidoresToleranValoresAntiguos() {
        val convertidores = ConvertidoresHuerto()
        assertEquals("PENDIENTE_DE_VALIDACION", convertidores.estadoTareaATexto(EstadoTarea.PENDIENTE_DE_VALIDACION))
        assertEquals(EstadoTarea.VALIDADA, convertidores.textoAEstadoTarea("VALIDADA"))
        // Las tareas creadas antes con tipo "CUIDADO" no cierran la app.
        assertEquals(TipoTarea.OTRA, convertidores.textoATipoTarea("CUIDADO"))
        assertEquals(TipoCuenta.DOCENTE, convertidores.textoATipoCuenta("DOCENTE"))
    }
}
