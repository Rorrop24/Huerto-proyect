package cl.duoc.fotomotion.domain.model

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

enum class EstadoSector(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    EN_USO("En uso"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_USO("Fuera de uso");

    companion object {
        // Los sectores guardados sin un estado reconocido se consideran en uso.
        fun desde(codigo: String) = entries.find { estado -> estado.name == codigo } ?: EN_USO
    }
}

// Un sector fuera de uso no puede recibir nuevos cultivos, tareas ni reportes.
val ParcelaHuerto.admiteAsignaciones: Boolean
    get() = EstadoSector.desde(situacionParcela) != EstadoSector.FUERA_DE_USO
