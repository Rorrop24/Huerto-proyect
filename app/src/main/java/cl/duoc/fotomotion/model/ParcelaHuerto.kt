package cl.duoc.fotomotion.model

import androidx.room.*

@Entity(tableName = "sectores")
data class ParcelaHuerto(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val codigoParcela: Int = 0,
    @ColumnInfo(name = "nombre") val tituloParcela: String = "",
    @ColumnInfo(name = "descripcion") val detalleParcela: String = "",
    @ColumnInfo(name = "ubicacion") val lugarParcela: String = "",
    @ColumnInfo(name = "estado") val situacionParcela: String = "",
    @ColumnInfo(name = "responsableId") val encargadoParcela: String = "",
    @ColumnInfo(name = "fechaCreacion") val diaRegistro: String = "",
    @ColumnInfo(name = "fechaActualizacion") val diaCambio: String = ""
)
