package cl.duoc.fotomotion.domain.model

import androidx.room.*

// Usuario registrado. La contraseña nunca se guarda: solo su hash PBKDF2 y la sal usada.
@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "rol") val rol: TipoCuenta,
    @ColumnInfo(name = "hashContrasena") val hashContrasena: String,
    @ColumnInfo(name = "sal") val sal: String
) {
    // El código "USR-001" es el que se guarda en tareas y reportes como responsable o autor.
    // Es una función (no una propiedad) para que Room no la confunda con una columna.
    fun aCuenta() = Cuenta(codigoParaId(id), nombre, rol)

    companion object {
        fun codigoParaId(id: Int) = "USR-" + id.toString().padStart(3, '0')
    }
}
