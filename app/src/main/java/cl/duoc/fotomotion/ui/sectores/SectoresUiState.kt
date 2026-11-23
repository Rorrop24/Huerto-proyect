package cl.duoc.fotomotion.ui.sectores

import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.Sector

data class SectoresUiState(
    val sectores: List<Sector> = emptyList(),
    val formularioVisible: Boolean = false,
    val textoNombre: String = "",
    val textoUbicacion: String = "",
    val estadoElegido: EstadoSector = EstadoSector.EN_USO,
    val errorNombre: String = "",
    val errorUbicacion: String = "",
    val sectorEnEdicion: Sector? = null,
    val aviso: String = "",
    val guardando: Boolean = false
)
