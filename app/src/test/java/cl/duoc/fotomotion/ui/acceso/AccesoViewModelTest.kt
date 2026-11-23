package cl.duoc.fotomotion.ui.acceso

import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.prueba.HuertoDaoEnMemoria
import cl.duoc.fotomotion.prueba.Datos
import cl.duoc.fotomotion.prueba.ReglaCorrutinas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AccesoViewModelTest {
    @get:Rule val reglaCorrutinas = ReglaCorrutinas()
    private val dao = HuertoDaoEnMemoria().apply { agregarCuentasDemo() }
    private val viewModel = AccesoViewModel(Datos.repositorioCuentas(dao))

    @Test
    fun ingresaConLaCuentaDelDocente() = runTest {
        viewModel.escribirCorreo("docente@huerto.cl")
        viewModel.escribirContrasena("Docente2026")
        viewModel.ingresar()
        val cuenta = viewModel.uiState.value.cuentaActiva
        assertEquals(TipoCuenta.DOCENTE, cuenta?.tipo)
        assertEquals("USR-001", cuenta?.codigo)
        // La contraseña no queda guardada en el estado después de entrar.
        assertEquals("", viewModel.uiState.value.textoContrasena)
    }

    @Test
    fun conContrasenaIncorrectaNoEntraYBorraLaContrasena() = runTest {
        viewModel.escribirCorreo("mateo@huerto.cl")
        viewModel.escribirContrasena("Incorrecta1")
        viewModel.ingresar()
        val estado = viewModel.uiState.value
        assertNull(estado.cuentaActiva)
        assertEquals("Correo o contraseña incorrectos.", estado.aviso)
        assertEquals("mateo@huerto.cl", estado.textoCorreo)
        assertEquals("", estado.textoContrasena)
    }

    @Test
    fun validaElFormularioAntesDeConsultar() = runTest {
        viewModel.ingresar()
        assertEquals("Escribe tu correo.", viewModel.uiState.value.errorCorreo)
        assertEquals("Escribe tu contraseña.", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun registraUnEstudianteNuevoYNoDuplicaCorreos() = runTest {
        viewModel.cambiarModo(ModoAcceso.REGISTRARSE)
        viewModel.escribirNombre("Ana Pérez")
        viewModel.escribirCorreo("ana@huerto.cl")
        viewModel.escribirContrasena("Huerto123")
        viewModel.escribirConfirmacion("Huerto123")
        viewModel.registrarse()
        assertEquals(TipoCuenta.ALUMNO, viewModel.uiState.value.cuentaActiva?.tipo)

        viewModel.salirDeCuenta()
        viewModel.cambiarModo(ModoAcceso.REGISTRARSE)
        viewModel.escribirNombre("Otra Ana")
        viewModel.escribirCorreo("ana@huerto.cl")
        viewModel.escribirContrasena("Huerto456")
        viewModel.escribirConfirmacion("Huerto456")
        viewModel.registrarse()
        assertEquals("Ese correo ya está registrado.", viewModel.uiState.value.errorCorreo)
        assertNull(viewModel.uiState.value.cuentaActiva)
    }

    @Test
    fun elRegistroValidaLaConfirmacion() = runTest {
        viewModel.cambiarModo(ModoAcceso.REGISTRARSE)
        viewModel.escribirNombre("Ana")
        viewModel.escribirCorreo("ana@huerto.cl")
        viewModel.escribirContrasena("Huerto123")
        viewModel.escribirConfirmacion("Huerto999")
        viewModel.registrarse()
        assertEquals("Las contraseñas no coinciden.", viewModel.uiState.value.errorConfirmacion)
    }

    @Test
    fun lasCuentasDemoCompletanElFormulario() = runTest {
        viewModel.usarCuentaDemo(viewModel.cuentasDemo[2])
        assertEquals("sofia@huerto.cl", viewModel.uiState.value.textoCorreo)
        viewModel.ingresar()
        assertEquals("Estudiante Sofía Muñoz", viewModel.uiState.value.cuentaActiva?.nombre)
        viewModel.salirDeCuenta()
        assertNull(viewModel.uiState.value.cuentaActiva)
    }

    @Test
    fun siLaBaseFallaSeAvisa() = runTest {
        dao.fallar = true
        viewModel.escribirCorreo("docente@huerto.cl")
        viewModel.escribirContrasena("Docente2026")
        viewModel.ingresar()
        assertEquals("No se pudo iniciar sesión. Intenta nuevamente.", viewModel.uiState.value.aviso)

        viewModel.cambiarModo(ModoAcceso.REGISTRARSE)
        viewModel.escribirNombre("Ana Pérez")
        viewModel.escribirCorreo("ana@huerto.cl")
        viewModel.escribirContrasena("Huerto123")
        viewModel.escribirConfirmacion("Huerto123")
        viewModel.registrarse()
        assertEquals("No se pudo crear la cuenta. Intenta nuevamente.", viewModel.uiState.value.aviso)
        assertFalse(viewModel.uiState.value.procesando)
    }
}
