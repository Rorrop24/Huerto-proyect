package cl.duoc.fotomotion.data.repository

import android.content.Context
import cl.duoc.fotomotion.data.local.BaseDatosHuerto
import cl.duoc.fotomotion.data.local.HuertoDao
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.Usuario
import cl.duoc.fotomotion.domain.seguridad.CifradorContrasenas
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Cuentas de usuario: iniciar sesión, registrarse y listar estudiantes.
// Las contraseñas nunca se guardan en texto plano: se guarda su hash con CifradorContrasenas.
// El despachador del cifrado se puede cambiar en las pruebas para que todo ocurra de inmediato.
class RepositorioCuentas(
    private val dao: HuertoDao,
    private val despachadorCifrado: CoroutineDispatcher = Dispatchers.Default
) {
    // Estudiantes a los que el docente puede asignar tareas.
    fun leerEstudiantes() = dao.observarUsuariosPorRol(TipoCuenta.ALUMNO)
        .map { usuarios -> usuarios.map(Usuario::aCuenta) }

    // Devuelve la cuenta si el correo existe y la contraseña coincide con el hash guardado; si no, null.
    suspend fun ingresar(correo: String, contrasena: String): Cuenta? {
        val usuario = dao.buscarUsuarioPorCorreo(correo.trim().lowercase()) ?: return null
        val coincide = withContext(despachadorCifrado) {
            CifradorContrasenas.verificar(contrasena, usuario.hashContrasena, usuario.sal)
        }
        return if (coincide) usuario.aCuenta() else null
    }

    // Las cuentas nuevas siempre son de estudiante; el docente es la cuenta administradora de demostración.
    // Devuelve null si el correo ya está registrado.
    suspend fun registrarEstudiante(nombre: String, correo: String, contrasena: String): Cuenta? {
        val correoNormalizado = correo.trim().lowercase()
        if (dao.buscarUsuarioPorCorreo(correoNormalizado) != null) return null
        val cifrada = withContext(despachadorCifrado) { CifradorContrasenas.cifrar(contrasena) }
        val usuarioNuevo = Usuario(nombre = nombre.trim(), correo = correoNormalizado,
            rol = TipoCuenta.ALUMNO, hashContrasena = cifrada.hash, sal = cifrada.sal)
        val idCreado = dao.agregarUsuario(usuarioNuevo)
        return usuarioNuevo.copy(id = idCreado.toInt()).aCuenta()
    }

    companion object {
        fun desdeContexto(contextoAplicacion: Context) =
            RepositorioCuentas(BaseDatosHuerto.abrirBase(contextoAplicacion).huertoDao())
    }
}
