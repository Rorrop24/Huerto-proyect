package cl.duoc.fotomotion.ui.parcelas

import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.ParcelaHuerto

data class EstadoParcelas(
    val listaParcelas: List<ParcelaHuerto> = emptyList(),
    val textoParcela: String = "",
    val textoLugar: String = "",
    val estadoElegido: EstadoSector = EstadoSector.EN_USO,
    val errorParcela: String = "",
    val errorLugar: String = "",
    val parcelaEnEdicion: ParcelaHuerto? = null,
    val avisoParcelas: String = "",
    val guardandoParcela: Boolean = false
)
