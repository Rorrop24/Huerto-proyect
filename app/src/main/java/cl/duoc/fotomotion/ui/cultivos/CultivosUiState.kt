package cl.duoc.fotomotion.ui.cultivos

import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import java.time.LocalDate

data class CultivosUiState(
    val cultivos: List<Cultivo> = emptyList(),
    val sectores: List<Sector> = emptyList(),
    val formularioVisible: Boolean = false,
    val textoNombre: String = "",
    val sectorElegido: Int? = null,
    val estadoCultivo: EstadoCultivo = EstadoCultivo.SEMBRADO,
    val textoFechaSiembra: String = LocalDate.now().toString(),
    val textoFechaTrasplante: String = "",
    val textoFechaCosecha: String = "",
    val textoRiego: String = "1",
    val errorNombre: String = "",
    val errorSector: String = "",
    val errorFechaSiembra: String = "",
    val errorFechaTrasplante: String = "",
    val errorFechaCosecha: String = "",
    val errorRiego: String = "",
    val cultivoEnEdicion: Cultivo? = null,
    val aviso: String = "",
    val guardando: Boolean = false
)
