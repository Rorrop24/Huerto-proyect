package cl.duoc.fotomotion.ui.siembras

import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto

data class EstadoSiembras(
    val listaSiembras: List<SiembraHuerto> = emptyList(),
    val opcionesParcela: List<ParcelaHuerto> = emptyList(),
    val textoSiembra: String = "",
    val parcelaElegida: Int? = null,
    val errorSiembra: String = "",
    val errorSeleccion: String = "",
    val siembraEnEdicion: SiembraHuerto? = null,
    val avisoSiembras: String = "",
    val guardandoSiembra: Boolean = false
)
