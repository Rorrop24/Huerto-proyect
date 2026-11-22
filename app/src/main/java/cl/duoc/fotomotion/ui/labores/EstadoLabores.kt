package cl.duoc.fotomotion.ui.labores

import cl.duoc.fotomotion.domain.model.LaborHuerto
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.model.TipoLabor
import java.time.LocalDate

data class EstadoLabores(
    val listaLabores: List<LaborHuerto> = emptyList(),
    val sectoresDisponibles: List<ParcelaHuerto> = emptyList(),
    val cultivosDisponibles: List<SiembraHuerto> = emptyList(),
    val textoDetalle: String = "",
    val textoFecha: String = LocalDate.now().toString(),
    val sectorTrabajo: Int? = null,
    val cultivoTrabajo: Int? = null,
    val tipoLabor: TipoLabor = TipoLabor.OTRA,
    val errorDetalle: String = "",
    val errorFecha: String = "",
    val errorSector: String = "",
    val errorCultivo: String = "",
    val laborEnEdicion: LaborHuerto? = null,
    val laborEnEntrega: LaborHuerto? = null,
    val direccionFoto: String = "",
    val textoComentario: String = "",
    val errorFoto: String = "",
    val errorComentario: String = "",
    val avisoLabores: String = "",
    val guardandoLabor: Boolean = false
)
