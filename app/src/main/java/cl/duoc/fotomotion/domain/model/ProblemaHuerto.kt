package cl.duoc.fotomotion.domain.model

import androidx.room.*

@Entity(tableName = "problemas")
data class ProblemaHuerto(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val codigoProblema: Int = 0,
    @ColumnInfo(name = "sectorId") val parcelaAfectada: Int,
    @ColumnInfo(name = "cultivoId") val siembraAfectada: Int? = null,
    @ColumnInfo(name = "tipo") val claseProblema: String = "",
    @ColumnInfo(name = "descripcion") val detalleProblema: String = "",
    @ColumnInfo(name = "fechaHoraReporte") val momentoReporte: String = "",
    @ColumnInfo(name = "autorId") val autorReporte: String = "",
    @ColumnInfo(name = "fotografiaAdjunta") val imagenProblema: String? = null,
    @ColumnInfo(name = "estado") val situacionProblema: String = "",
    @ColumnInfo(name = "comentarioDocente") val notaSeguimiento: String? = null,
    @ColumnInfo(name = "fechaCierre") val momentoCierre: String? = null
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
