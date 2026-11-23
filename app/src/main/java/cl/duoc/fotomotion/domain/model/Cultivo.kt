package cl.duoc.fotomotion.domain.model

import androidx.room.*

@Entity(tableName = "cultivos")
data class Cultivo(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombreComun") val nombre: String = "",
    @ColumnInfo(name = "descripcion") val descripcion: String = "",
    @ColumnInfo(name = "sectorId") val sectorId: Int,
    @ColumnInfo(name = "fechaSiembra") val fechaSiembra: String? = null,
    @ColumnInfo(name = "fechaTrasplante") val fechaTrasplante: String? = null,
    @ColumnInfo(name = "fechaEstimadaCosecha") val fechaCosecha: String? = null,
    @ColumnInfo(name = "frecuenciaRiegoDias") val frecuenciaRiegoDias: Int,
    @ColumnInfo(name = "estado") val estado: EstadoCultivo = EstadoCultivo.SEMBRADO,
    @ColumnInfo(name = "observaciones") val observaciones: String = "",
    @ColumnInfo(name = "fotografiaReferencialFicticia") val fotografia: String? = null
)

// "activo" indica si el cultivo está en la tierra y necesita riego o cosecha.
enum class EstadoCultivo(val etiqueta: String, val activo: Boolean) {
    PLANIFICADO("Planificado", false),
    SEMBRADO("Sembrado", true),
    EN_CRECIMIENTO("En crecimiento", true),
    LISTO_PARA_COSECHAR("Listo para cosechar", true),
    COSECHADO("Cosechado", false),
    CON_PROBLEMAS("Con problemas", true),
    FINALIZADO("Finalizado", false);

    companion object {
        fun desde(codigo: String) = entries.find { estado -> estado.name == codigo } ?: SEMBRADO
    }
}
