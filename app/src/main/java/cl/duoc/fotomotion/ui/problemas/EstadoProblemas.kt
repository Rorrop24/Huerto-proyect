package cl.duoc.fotomotion.ui.problemas

import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.ProblemaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.model.TipoProblema

data class EstadoProblemas(
    val listaProblemas: List<ProblemaHuerto> = emptyList(),
    val sectoresDisponibles: List<ParcelaHuerto> = emptyList(),
    val cultivosDisponibles: List<SiembraHuerto> = emptyList(),
    val sectorElegido: Int? = null,
    val cultivoElegido: Int? = null,
    val tipoElegido: TipoProblema? = null,
    val textoDescripcion: String = "",
    val direccionFoto: String = "",
    val errorSector: String = "",
    val errorCultivo: String = "",
    val errorTipo: String = "",
    val errorDescripcion: String = "",
    val problemaEnSeguimiento: ProblemaHuerto? = null,
    val estadoSeguimiento: EstadoProblema = EstadoProblema.INFORMADO,
    val textoSeguimiento: String = "",
    val errorSeguimiento: String = "",
    val avisoProblemas: String = "",
    val guardandoProblema: Boolean = false
)
