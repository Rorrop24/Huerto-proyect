package cl.duoc.fotomotion.domain

import java.time.LocalDate
import cl.duoc.fotomotion.domain.model.estaAtrasada
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.domain.validation.ValidadorAcceso
import cl.duoc.fotomotion.domain.validation.ValidadorTareas
import cl.duoc.fotomotion.domain.validation.ValidadorSectores
import cl.duoc.fotomotion.domain.validation.ValidadorProblemas
import cl.duoc.fotomotion.domain.validation.ValidadorCultivos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {
    private val sectores = listOf(
        Sector(id = 1, nombre = "Invernadero"),
        Sector(id = 2, nombre = "Compostera", estado = EstadoSector.FUERA_DE_USO)
    )
    private val cultivos = listOf(Cultivo(id = 7, sectorId = 1, frecuenciaRiegoDias = 2))
    private val estudiantes = listOf(Cuenta("USR-002", "Mateo", TipoCuenta.ALUMNO))

    @Test
    fun ingresoPideCorreoValidoYContrasena() {
        val errores = ValidadorAcceso.validarIngreso("correo-malo", "")
        assertEquals("Escribe un correo válido, por ejemplo nombre@huerto.cl.", errores.correo)
        assertEquals("Escribe tu contraseña.", errores.contrasena)
        assertTrue(ValidadorAcceso.validarIngreso("docente@huerto.cl", "Docente2026").esValido)
    }

    @Test
    fun registroExigeContrasenaSeguraYConfirmada() {
        assertEquals("Usa al menos 8 caracteres.", ValidadorAcceso.validarRegistro("Ana", "ana@huerto.cl", "abc1", "abc1").contrasena)
        assertEquals("Combina letras y números.", ValidadorAcceso.validarRegistro("Ana", "ana@huerto.cl", "soloLetras", "soloLetras").contrasena)
        assertEquals("Las contraseñas no coinciden.", ValidadorAcceso.validarRegistro("Ana", "ana@huerto.cl", "Huerto123", "Huerto124").confirmacion)
        assertTrue(ValidadorAcceso.validarRegistro("Ana Pérez", "ana@huerto.cl", "Huerto123", "Huerto123").esValido)
    }

    @Test
    fun sectorNecesitaNombreYUbicacion() {
        val errores = ValidadorSectores.validar("", "ab")
        assertEquals("Escribe el nombre del sector.", errores.nombre)
        assertEquals("Usa entre 3 y 80 caracteres.", errores.ubicacion)
        assertTrue(ValidadorSectores.validar("Invernadero", "Patio norte").esValido)
    }

    @Test
    fun cultivoRevisaSectorFechasYRiego() {
        val errores = ValidadorCultivos.validar("Lechuga", 2, sectores, "2026-05-10", "2026-05-01", "2026-04-01", "0")
        assertEquals("El sector ya no está disponible.", errores.sector)
        assertEquals("El trasplante no puede ser antes de la siembra.", errores.fechaTrasplante)
        assertEquals("La cosecha no puede ser antes de la siembra.", errores.fechaCosecha)
        assertEquals("Usa un número entre 1 y 30.", errores.riego)
        assertTrue(ValidadorCultivos.validar("Lechuga", 1, sectores, "2026-05-10", "", "2026-07-01", "2").esValido)
    }

    @Test
    fun tareaDebeAsignarseAUnEstudiante() {
        val sinEstudiante = ValidadorTareas.validarTarea("Regar lechugas", "2026-11-30", 1, sectores, 7, cultivos, null, estudiantes)
        assertEquals("Asigna la tarea a un estudiante.", sinEstudiante.estudiante)
        val inexistente = ValidadorTareas.validarTarea("Regar lechugas", "2026-11-30", 1, sectores, 7, cultivos, "USR-099", estudiantes)
        assertEquals("El estudiante ya no está disponible.", inexistente.estudiante)
        assertTrue(ValidadorTareas.validarTarea("Regar lechugas", "2026-11-30", 1, sectores, 7, cultivos, "USR-002", estudiantes).esValido)
    }

    @Test
    fun entregaNecesitaFotoYComentario() {
        val errores = ValidadorTareas.validarEntrega("", "ok")
        assertEquals("Adjunta una foto con cámara o galería.", errores.foto)
        assertEquals("Usa entre 5 y 300 caracteres.", errores.comentario)
        assertTrue(ValidadorTareas.validarEntrega("content://foto/1", "Regué todo el sector").esValido)
    }

    @Test
    fun reporteNecesitaTipoYDescripcion() {
        val errores = ValidadorProblemas.validarReporte(1, sectores, null, cultivos, null, "")
        assertEquals("Elige el tipo de problema.", errores.tipo)
        assertEquals("Describe brevemente el problema.", errores.descripcion)
        assertFalse(errores.esValido)
        assertTrue(ValidadorProblemas.validarReporte(1, sectores, 7, cultivos, TipoProblema.FALTA_AGUA, "Tierra seca").esValido)
        assertEquals("", ValidadorProblemas.validarSeguimiento(""))
        assertEquals("Usa entre 3 y 200 caracteres.", ValidadorProblemas.validarSeguimiento("ok"))
    }

    @Test
    fun casosLimiteDeLasReglasComunes() {
        assertEquals("Usa entre 3 y 5 caracteres.", ValidacionesHuerto.revisarTexto("demasiado largo", 3, 5, "Vacío"))
        assertEquals("Escribe una contraseña.", ValidacionesHuerto.revisarContrasenaNueva(""))
        assertEquals("Combina letras y números.", ValidacionesHuerto.revisarContrasenaNueva("12345678"))
        assertEquals("Escribe una fecha.", ValidacionesHuerto.revisarFecha(" "))
        assertEquals("El sector ya no está disponible.", ValidacionesHuerto.revisarSector(2, sectores, "Elige"))
        assertEquals("El sector ya no está disponible.", ValidacionesHuerto.revisarSector(9, sectores, "Elige"))
        assertEquals("El cultivo ya no está disponible.", ValidacionesHuerto.revisarCultivo(99, 1, cultivos))
        assertEquals("El cultivo no pertenece al sector elegido.", ValidacionesHuerto.revisarCultivo(7, 2, cultivos))
        assertEquals("", ValidacionesHuerto.revisarCultivo(7, 1, cultivos))
    }

    @Test
    fun cadaFormularioEsInvalidoSiFallaCualquierCampo() {
        assertFalse(ValidadorAcceso.validarIngreso("docente@huerto.cl", "").esValido)
        assertEquals("Repite la contraseña.", ValidadorAcceso.validarRegistro("Ana", "ana@huerto.cl", "Huerto123", "").confirmacion)
        assertFalse(ValidadorSectores.validar("Invernadero", "").esValido)
        assertFalse(ValidadorTareas.validarEntrega("content://foto/1", "").esValido)
        val errores = ValidadorCultivos.validar("Lechuga", 1, sectores, "2026-09-01", "2026-9-15", "1-12-2026", "3")
        assertEquals("Usa el formato AAAA-MM-DD.", errores.fechaTrasplante)
        assertEquals("Usa el formato AAAA-MM-DD.", errores.fechaCosecha)
        // Una fecha mal escrita no marca la tarea como atrasada.
        assertFalse(Tarea(sectorId = 1, fechaProgramada = "mañana").estaAtrasada(LocalDate.of(2026, 11, 11)))
    }
}
