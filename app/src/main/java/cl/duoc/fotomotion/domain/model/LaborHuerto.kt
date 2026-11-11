package cl.duoc.fotomotion.domain.model

import androidx.room.*
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import java.time.LocalDate

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

enum class TipoLabor(val etiqueta: String) {
    PREPARACION("Preparación del sector"),
    SIEMBRA("Siembra"),
    TRASPLANTE("Trasplante"),
    RIEGO("Riego"),
    LIMPIEZA("Limpieza"),
    RETIRO_MALEZAS("Retiro de malezas"),
    OBSERVACION("Observación del cultivo"),
    MANTENIMIENTO("Mantenimiento"),
    REVISION_SUELO("Revisión del suelo"),
    REGISTRO_CRECIMIENTO("Registro del crecimiento"),
    COSECHA("Cosecha"),
    OTRA("Otra actividad autorizada");

    companion object {
        // Las tareas antiguas (tipo "CUIDADO") se muestran como otra actividad.
        fun desde(codigo: String) = entries.find { tipo -> tipo.name == codigo } ?: OTRA
    }
}

// Una tarea sin terminar cuya fecha programada ya pasó se considera atrasada.
fun LaborHuerto.estaAtrasada(hoy: LocalDate): Boolean {
    if (situacionLabor != "PENDIENTE" && situacionLabor != "RECHAZADA") return false
    val fechaProgramada = ValidacionesHuerto.leerFecha(diaPlanificado) ?: return false
    return fechaProgramada.isBefore(hoy)
}
