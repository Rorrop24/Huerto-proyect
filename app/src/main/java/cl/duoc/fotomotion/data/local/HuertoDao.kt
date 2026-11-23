package cl.duoc.fotomotion.data.local

import androidx.room.*
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface HuertoDao {
    @Insert suspend fun agregarSector(sector: Sector)
    @Update suspend fun actualizarSector(sector: Sector)
    @Delete suspend fun eliminarSector(sector: Sector)
    @Query("SELECT * FROM sectores ORDER BY id DESC")
    fun observarSectores(): Flow<List<Sector>>

    @Insert suspend fun agregarCultivo(cultivo: Cultivo)
    @Update suspend fun actualizarCultivo(cultivo: Cultivo)
    @Delete suspend fun eliminarCultivo(cultivo: Cultivo)
    @Query("SELECT * FROM cultivos ORDER BY id DESC")
    fun observarCultivos(): Flow<List<Cultivo>>

    @Insert suspend fun agregarTarea(tarea: Tarea)
    @Update suspend fun actualizarTarea(tarea: Tarea)
    @Delete suspend fun eliminarTarea(tarea: Tarea)
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarTareas(): Flow<List<Tarea>>

    @Insert suspend fun agregarProblema(problema: Problema)
    @Update suspend fun actualizarProblema(problema: Problema)
    @Query("SELECT * FROM problemas ORDER BY id DESC")
    fun observarProblemas(): Flow<List<Problema>>

    @Insert suspend fun agregarUsuario(usuario: Usuario): Long
    @Query("SELECT * FROM usuarios WHERE correo = :correoBuscado LIMIT 1")
    suspend fun buscarUsuarioPorCorreo(correoBuscado: String): Usuario?
    @Query("SELECT * FROM usuarios WHERE rol = :rolBuscado ORDER BY nombre")
    fun observarUsuariosPorRol(rolBuscado: TipoCuenta): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM cultivos WHERE sectorId = :codigoBuscado")
    suspend fun contarCultivosDeSector(codigoBuscado: Int): Int
    @Query("SELECT COUNT(*) FROM tareas WHERE sectorId = :codigoBuscado")
    suspend fun contarTareasDeSector(codigoBuscado: Int): Int
    @Query("SELECT COUNT(*) FROM tareas WHERE cultivoId = :codigoBuscado")
    suspend fun contarTareasDeCultivo(codigoBuscado: Int): Int
}
