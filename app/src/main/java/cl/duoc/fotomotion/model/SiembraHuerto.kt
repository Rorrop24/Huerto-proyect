package cl.duoc.fotomotion.model

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
