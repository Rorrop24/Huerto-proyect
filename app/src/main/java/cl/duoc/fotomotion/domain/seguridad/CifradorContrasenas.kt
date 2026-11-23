package cl.duoc.fotomotion.domain.seguridad

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Convierte la contraseña en un hash PBKDF2 con una sal aleatoria. Así nunca se guarda en texto plano.
object CifradorContrasenas {
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 20_000
    private const val LARGO_BITS = 256

    data class ContrasenaCifrada(val hash: String, val sal: String)

    fun cifrar(contrasena: String): ContrasenaCifrada {
        val sal = ByteArray(16).also { bytes -> SecureRandom().nextBytes(bytes) }
        return ContrasenaCifrada(hash = calcular(contrasena, sal), sal = codificar(sal))
    }

    fun verificar(contrasena: String, hashGuardado: String, salGuardada: String): Boolean {
        val hashNuevo = calcular(contrasena, Base64.getDecoder().decode(salGuardada))
        // Comparación en tiempo constante para no dar pistas por la demora.
        return MessageDigest.isEqual(hashNuevo.toByteArray(), hashGuardado.toByteArray())
    }

    private fun calcular(contrasena: String, sal: ByteArray): String {
        val especificacion = PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        return codificar(SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).encoded)
    }

    private fun codificar(bytes: ByteArray) = Base64.getEncoder().encodeToString(bytes)
}
