package cl.duoc.fotomotion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.fotomotion.domain.model.CuentasDemo
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.Usuario
import cl.duoc.fotomotion.domain.seguridad.CifradorContrasenas

@Database(entities = [Sector::class, Cultivo::class, Tarea::class, Problema::class, Usuario::class],
    version = 3, exportSchema = true)
@TypeConverters(ConvertidoresHuerto::class)
abstract class BaseDatosHuerto : RoomDatabase() {
    abstract fun huertoDao(): HuertoDao

    companion object {
        @Volatile private var baseAbierta: BaseDatosHuerto? = null

        // Versión 2: agrega la tabla de problemas reportados sin borrar los datos existentes.
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `problemas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sectorId` INTEGER NOT NULL, `cultivoId` INTEGER, `tipo` TEXT NOT NULL, `descripcion` TEXT NOT NULL, " +
                    "`fechaHoraReporte` TEXT NOT NULL, `autorId` TEXT NOT NULL, `fotografiaAdjunta` TEXT, " +
                    "`estado` TEXT NOT NULL, `comentarioDocente` TEXT, `fechaCierre` TEXT)")
            }
        }

        // Versión 3: agrega la tabla de usuarios y las cuentas de demostración.
        val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `usuarios` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nombre` TEXT NOT NULL, `correo` TEXT NOT NULL, `rol` TEXT NOT NULL, " +
                    "`hashContrasena` TEXT NOT NULL, `sal` TEXT NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
                sembrarCuentasDemo(db)
            }
        }

        // En una instalación nueva Room crea las tablas y después se agregan las cuentas de demostración.
        private val alCrearBase = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                sembrarCuentasDemo(db)
            }
        }

        // "INSERT OR IGNORE" junto al correo único evita cuentas duplicadas aunque se ejecute más de una vez.
        fun sembrarCuentasDemo(db: SupportSQLiteDatabase) {
            CuentasDemo.lista.forEach { cuenta ->
                val cifrada = CifradorContrasenas.cifrar(cuenta.contrasena)
                db.execSQL("INSERT OR IGNORE INTO `usuarios` (`id`, `nombre`, `correo`, `rol`, `hashContrasena`, `sal`) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf<Any>(cuenta.id, cuenta.nombre, cuenta.correo, cuenta.rol.name, cifrada.hash, cifrada.sal))
            }
        }

        fun abrirBase(contextoAplicacion: Context): BaseDatosHuerto {
            return baseAbierta ?: synchronized(this) {
                val baseCreada = baseAbierta ?: Room.databaseBuilder(
                    contextoAplicacion.applicationContext,
                    BaseDatosHuerto::class.java,
                    "huerto_db.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3).addCallback(alCrearBase).build()
                baseAbierta = baseCreada
                baseCreada
            }
        }
    }
}
