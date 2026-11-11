package cl.duoc.fotomotion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.fotomotion.domain.model.LaborHuerto
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.ProblemaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto

@Database(entities = [ParcelaHuerto::class, SiembraHuerto::class, LaborHuerto::class, ProblemaHuerto::class],
    version = 2, exportSchema = true)
abstract class BaseHuerto : RoomDatabase() {
    abstract fun consultasHuerto(): ConsultasHuerto

    companion object {
        @Volatile private var baseAbierta: BaseHuerto? = null

        // Versión 2: agrega la tabla de problemas reportados sin borrar los datos existentes.
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `problemas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sectorId` INTEGER NOT NULL, `cultivoId` INTEGER, `tipo` TEXT NOT NULL, `descripcion` TEXT NOT NULL, " +
                    "`fechaHoraReporte` TEXT NOT NULL, `autorId` TEXT NOT NULL, `fotografiaAdjunta` TEXT, " +
                    "`estado` TEXT NOT NULL, `comentarioDocente` TEXT, `fechaCierre` TEXT)")
            }
        }

        fun abrirBase(contextoAplicacion: Context): BaseHuerto {
            return baseAbierta ?: synchronized(this) {
                val baseCreada = baseAbierta ?: Room.databaseBuilder(
                    contextoAplicacion.applicationContext,
                    BaseHuerto::class.java,
                    "huerto_db.db"
                ).addMigrations(MIGRACION_1_2).build()
                baseAbierta = baseCreada
                baseCreada
            }
        }
    }
}
