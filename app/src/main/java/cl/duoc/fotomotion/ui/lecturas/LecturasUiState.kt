package cl.duoc.fotomotion.ui.lecturas

import cl.duoc.fotomotion.domain.model.LecturaSensor

data class LecturasUiState(
    val lecturas: List<LecturaSensor> = emptyList(),
    val cantidadSimulaciones: Int = 0
)
