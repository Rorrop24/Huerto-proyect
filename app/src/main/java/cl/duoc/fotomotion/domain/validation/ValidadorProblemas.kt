package cl.duoc.fotomotion.domain.validation

import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoProblema

data class ErroresProblema(val sector: String = "", val cultivo: String = "", val tipo: String = "", val descripcion: String = "") {
    val esValido: Boolean get() = listOf(sector, cultivo, tipo, descripcion).all { error -> error.isEmpty() }
}

// Reglas del reporte de problemas y del comentario de seguimiento.
object ValidadorProblemas {
    fun validarReporte(sectorElegido: Int?, sectores: List<Sector>, cultivoElegido: Int?,
        cultivos: List<Cultivo>, tipo: TipoProblema?, descripcion: String) = ErroresProblema(
        sector = ValidacionesHuerto.revisarSector(sectorElegido, sectores, "Selecciona el sector afectado."),
        cultivo = ValidacionesHuerto.revisarCultivo(cultivoElegido, sectorElegido, cultivos),
        tipo = if (tipo == null) "Elige el tipo de problema." else "",
        descripcion = ValidacionesHuerto.revisarTexto(descripcion, 5, 200, "Describe brevemente el problema.")
    )

    fun validarSeguimiento(comentario: String): String = ValidacionesHuerto.revisarTextoOpcional(comentario, 3, 200)
}
