package cl.duoc.fotomotion.domain.model

import androidx.room.*

@Entity(tableName = "problemas")
data class Problema(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "sectorId") val sectorId: Int,
    @ColumnInfo(name = "cultivoId") val cultivoId: Int? = null,
    @ColumnInfo(name = "tipo") val tipo: TipoProblema = TipoProblema.OTRO,
    @ColumnInfo(name = "descripcion") val descripcion: String = "",
    @ColumnInfo(name = "fechaHoraReporte") val fechaReporte: String = "",
    @ColumnInfo(name = "autorId") val autorId: String = "",
    @ColumnInfo(name = "fotografiaAdjunta") val fotografia: String? = null,
    @ColumnInfo(name = "estado") val estado: EstadoProblema = EstadoProblema.INFORMADO,
    @ColumnInfo(name = "comentarioDocente") val comentarioDocente: String? = null,
    @ColumnInfo(name = "fechaCierre") val fechaCierre: String? = null
)

enum class TipoProblema(val etiqueta: String) {
    FALTA_AGUA("Falta de agua"),
    DANO_PLANTA("Daño en la planta"),
    PLAGA_SIMULADA("Plaga simulada"),
    DETERIORO_SECTOR("Deterioro del sector"),
    MANTENIMIENTO("Problema de mantenimiento"),
    OTRO("Otro problema observado");

    companion object {
        fun desde(codigo: String) = entries.find { tipo -> tipo.name == codigo } ?: OTRO
    }
}

// "resuelto" indica si el reporte ya no requiere atención del docente.
enum class EstadoProblema(val etiqueta: String, val resuelto: Boolean) {
    INFORMADO("Informado", false),
    EN_REVISION("En revisión", false),
    ATENDIDO("Atendido", true),
    CERRADO("Cerrado", true);

    companion object {
        fun desde(codigo: String) = entries.find { estado -> estado.name == codigo } ?: INFORMADO
    }
}
