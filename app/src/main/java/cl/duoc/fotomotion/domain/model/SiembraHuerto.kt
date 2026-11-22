package cl.duoc.fotomotion.domain.model

import androidx.room.*

@Entity(tableName = "cultivos")
data class SiembraHuerto(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val codigoSiembra: Int = 0,
    @ColumnInfo(name = "nombreComun") val tituloSiembra: String = "",
    @ColumnInfo(name = "descripcion") val detalleSiembra: String = "",
    @ColumnInfo(name = "sectorId") val parcelaAsociada: Int,
    @ColumnInfo(name = "fechaSiembra") val diaSiembra: String? = null,
    @ColumnInfo(name = "fechaTrasplante") val diaTraslado: String? = null,
    @ColumnInfo(name = "fechaEstimadaCosecha") val diaCosecha: String? = null,
    @ColumnInfo(name = "frecuenciaRiegoDias") val intervaloRiego: Int,
    @ColumnInfo(name = "estado") val situacionSiembra: String = "",
    @ColumnInfo(name = "observaciones") val notasSiembra: String = "",
    @ColumnInfo(name = "fotografiaReferencialFicticia") val imagenSiembra: String? = null
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
