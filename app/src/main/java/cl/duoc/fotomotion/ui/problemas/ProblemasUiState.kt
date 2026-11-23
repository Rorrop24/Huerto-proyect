package cl.duoc.fotomotion.ui.problemas

import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoProblema

data class ProblemasUiState(
    val problemas: List<Problema> = emptyList(),
    val sectores: List<Sector> = emptyList(),
    val cultivos: List<Cultivo> = emptyList(),
    val formularioVisible: Boolean = false,
    val sectorElegido: Int? = null,
    val cultivoElegido: Int? = null,
    val tipoElegido: TipoProblema? = null,
    val textoDescripcion: String = "",
    val direccionFoto: String = "",
    val errorSector: String = "",
    val errorCultivo: String = "",
    val errorTipo: String = "",
    val errorDescripcion: String = "",
    val problemaEnSeguimiento: Problema? = null,
    val estadoSeguimiento: EstadoProblema = EstadoProblema.INFORMADO,
    val textoSeguimiento: String = "",
    val errorSeguimiento: String = "",
    val aviso: String = "",
    val guardando: Boolean = false
)
