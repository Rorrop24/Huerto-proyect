package cl.duoc.fotomotion.model

import androidx.room.*

@Entity(tableName = "tareas")
data class LaborHuerto(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val codigoLabor: Int = 0,
    @ColumnInfo(name = "tipo") val claseLabor: String = "",
    @ColumnInfo(name = "descripcion") val detalleLabor: String = "",
    @ColumnInfo(name = "sectorId") val parcelaTrabajo: Int,
    @ColumnInfo(name = "cultivoId") val siembraTrabajo: Int? = null,
    @ColumnInfo(name = "fechaCreacion") val diaCreacion: String = "",
    @ColumnInfo(name = "fechaProgramada") val diaPlanificado: String = "",
    @ColumnInfo(name = "responsableId") val encargadoLabor: String = "",
    @ColumnInfo(name = "creadorId") val autorLabor: String = "",
    @ColumnInfo(name = "estado") val situacionLabor: String = "",
    @ColumnInfo(name = "fechaHoraRealizacion") val momentoEntrega: String? = null,
    @ColumnInfo(name = "fotografiaAdjunta") val imagenLabor: String? = null,
    @ColumnInfo(name = "comentarioEstudiante") val notaEntrega: String? = null,
    @ColumnInfo(name = "comentarioDocente") val notaRevision: String? = null,
    @ColumnInfo(name = "validadorId") val revisorLabor: String? = null,
    @ColumnInfo(name = "fechaHoraValidacion") val momentoRevision: String? = null
)
