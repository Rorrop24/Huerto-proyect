package cl.duoc.fotomotion.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParcelaHuerto::class, SiembraHuerto::class, LaborHuerto::class], version = 1, exportSchema = true)
abstract class BaseHuerto : RoomDatabase() {
    abstract fun consultasHuerto(): ConsultasHuerto

    companion object {
        @Volatile private var baseAbierta: BaseHuerto? = null

        fun abrirBase(contextoAplicacion: Context): BaseHuerto {
            return baseAbierta ?: synchronized(this) {
                val baseCreada = baseAbierta ?: Room.databaseBuilder(
                    contextoAplicacion.applicationContext,
                    BaseHuerto::class.java,
                    "huerto_db.db"
                ).build()
                baseAbierta = baseCreada
                baseCreada
            }
        }
    }
}
