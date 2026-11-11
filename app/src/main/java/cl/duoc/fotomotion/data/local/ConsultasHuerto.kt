package cl.duoc.fotomotion.data.local

import androidx.room.*
import cl.duoc.fotomotion.domain.model.LaborHuerto
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.ProblemaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultasHuerto {
    @Insert suspend fun agregarParcela(registroParcela: ParcelaHuerto)
    @Update suspend fun modificarParcela(registroParcela: ParcelaHuerto)
    @Delete suspend fun quitarParcela(registroParcela: ParcelaHuerto)
    @Query("SELECT * FROM sectores ORDER BY id DESC")
    fun observarParcelas(): Flow<List<ParcelaHuerto>>

    @Insert suspend fun agregarSiembra(registroSiembra: SiembraHuerto)
    @Update suspend fun modificarSiembra(registroSiembra: SiembraHuerto)
    @Delete suspend fun quitarSiembra(registroSiembra: SiembraHuerto)
    @Query("SELECT * FROM cultivos ORDER BY id DESC")
    fun observarSiembras(): Flow<List<SiembraHuerto>>

    @Insert suspend fun agregarLabor(registroLabor: LaborHuerto)
    @Update suspend fun modificarLabor(registroLabor: LaborHuerto)
    @Delete suspend fun quitarLabor(registroLabor: LaborHuerto)
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarLabores(): Flow<List<LaborHuerto>>

    @Insert suspend fun agregarProblema(registroProblema: ProblemaHuerto)
    @Update suspend fun modificarProblema(registroProblema: ProblemaHuerto)
    @Query("SELECT * FROM problemas ORDER BY id DESC")
    fun observarProblemas(): Flow<List<ProblemaHuerto>>

    @Query("SELECT COUNT(*) FROM cultivos WHERE sectorId = :codigoBuscado")
    suspend fun contarSiembrasDeParcela(codigoBuscado: Int): Int
    @Query("SELECT COUNT(*) FROM tareas WHERE sectorId = :codigoBuscado")
    suspend fun contarLaboresDeParcela(codigoBuscado: Int): Int
    @Query("SELECT COUNT(*) FROM tareas WHERE cultivoId = :codigoBuscado")
    suspend fun contarLaboresDeSiembra(codigoBuscado: Int): Int
}
