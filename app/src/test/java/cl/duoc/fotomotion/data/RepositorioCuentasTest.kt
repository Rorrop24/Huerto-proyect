package cl.duoc.fotomotion.data

import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RepositorioCuentasTest {
    private val dao = HuertoDaoEnMemoria()
    private val repositorioCuentas = Datos.repositorioCuentas(dao)

    @Test
    fun soloListaEstudiantesYVerificaContrasenas() = runTest {
        dao.agregarCuentasDemo()
        assertEquals(3, repositorioCuentas.leerEstudiantes().first().size)
        assertEquals("USR-004", repositorioCuentas.ingresar(" Diego@Huerto.cl ", "Diego2026")?.codigo)
        assertNull(repositorioCuentas.ingresar("nadie@huerto.cl", "Diego2026"))
        assertNull(repositorioCuentas.ingresar("diego@huerto.cl", "Otra2026"))
    }

    @Test
    fun registraEstudiantesSinRepetirCorreo() = runTest {
        val cuenta = repositorioCuentas.registrarEstudiante(" Ana Pérez ", "Ana@Huerto.cl", "Huerto123")
        assertEquals("Ana Pérez", cuenta?.nombre)
        assertNull(repositorioCuentas.registrarEstudiante("Otra Ana", "ana@huerto.cl", "Huerto456"))
        assertEquals(cuenta, repositorioCuentas.ingresar("ana@huerto.cl", "Huerto123"))
        // La contraseña no queda guardada en texto plano.
        assertNotEquals("Huerto123", dao.buscarUsuarioPorCorreo("ana@huerto.cl")?.hashContrasena)
    }
}
