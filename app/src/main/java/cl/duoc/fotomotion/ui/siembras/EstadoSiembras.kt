package cl.duoc.fotomotion.ui.siembras

import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import java.time.LocalDate

data class EstadoSiembras(
    val listaSiembras: List<SiembraHuerto> = emptyList(),
    val opcionesParcela: List<ParcelaHuerto> = emptyList(),
    val textoSiembra: String = "",
    val parcelaElegida: Int? = null,
    val estadoCultivo: EstadoCultivo = EstadoCultivo.SEMBRADO,
    val textoFechaSiembra: String = LocalDate.now().toString(),
    val textoFechaTraslado: String = "",
    val textoFechaCosecha: String = "",
    val textoRiego: String = "1",
    val errorSiembra: String = "",
    val errorSeleccion: String = "",
    val errorFechaSiembra: String = "",
    val errorFechaTraslado: String = "",
    val errorFechaCosecha: String = "",
    val errorRiego: String = "",
    val siembraEnEdicion: SiembraHuerto? = null,
    val avisoSiembras: String = "",
    val guardandoSiembra: Boolean = false
)
