package cl.duoc.fotomotion.domain.model

import androidx.room.*
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import java.time.LocalDate

@Entity(tableName = "tareas")
data class Tarea(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "tipo") val tipo: TipoTarea = TipoTarea.OTRA,
    @ColumnInfo(name = "descripcion") val descripcion: String = "",
    @ColumnInfo(name = "sectorId") val sectorId: Int,
    @ColumnInfo(name = "cultivoId") val cultivoId: Int? = null,
    @ColumnInfo(name = "fechaCreacion") val fechaCreacion: String = "",
    @ColumnInfo(name = "fechaProgramada") val fechaProgramada: String = "",
    @ColumnInfo(name = "responsableId") val responsableId: String = "",
    @ColumnInfo(name = "creadorId") val creadorId: String = "",
    @ColumnInfo(name = "estado") val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    @ColumnInfo(name = "fechaHoraRealizacion") val fechaEntrega: String? = null,
    @ColumnInfo(name = "fotografiaAdjunta") val fotografia: String? = null,
    @ColumnInfo(name = "comentarioEstudiante") val comentarioEstudiante: String? = null,
    @ColumnInfo(name = "comentarioDocente") val comentarioDocente: String? = null,
    @ColumnInfo(name = "validadorId") val validadorId: String? = null,
    @ColumnInfo(name = "fechaHoraValidacion") val fechaValidacion: String? = null
)

enum class TipoTarea(val etiqueta: String) {
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

// Estados de una tarea. Reemplaza los textos "PENDIENTE", "VALIDADA", etc. que antes se comparaban a mano.
enum class EstadoTarea(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    PENDIENTE_DE_VALIDACION("En revisión"),
    VALIDADA("Aprobada"),
    RECHAZADA("Requiere corrección");

    // El estudiante puede entregar una tarea nueva o una que el docente devolvió.
    val admiteEntrega: Boolean get() = this == PENDIENTE || this == RECHAZADA

    companion object {
        fun desde(codigo: String) = entries.find { estado -> estado.name == codigo } ?: PENDIENTE
    }
}

// Una tarea sin terminar cuya fecha programada ya pasó se considera atrasada.
fun Tarea.estaAtrasada(hoy: LocalDate): Boolean {
    if (!estado.admiteEntrega) return false
    val fecha = ValidacionesHuerto.leerFecha(fechaProgramada) ?: return false
    return fecha.isBefore(hoy)
}

// El docente ve todas las tareas; cada estudiante solo las que tiene asignadas.
fun List<Tarea>.visiblesPara(cuenta: Cuenta?): List<Tarea> = when {
    cuenta == null -> emptyList()
    cuenta.esDocente -> this
    else -> filter { tarea -> tarea.responsableId == cuenta.codigo }
}
