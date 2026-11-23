package cl.duoc.fotomotion.domain.model

import androidx.room.*

@Entity(tableName = "sectores")
data class Sector(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "descripcion") val descripcion: String = "",
    @ColumnInfo(name = "ubicacion") val ubicacion: String = "",
    @ColumnInfo(name = "estado") val estado: EstadoSector = EstadoSector.EN_USO,
    @ColumnInfo(name = "responsableId") val responsableId: String = "",
    @ColumnInfo(name = "fechaCreacion") val fechaCreacion: String = "",
    @ColumnInfo(name = "fechaActualizacion") val fechaActualizacion: String = ""
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
val Sector.admiteAsignaciones: Boolean
    get() = estado != EstadoSector.FUERA_DE_USO
