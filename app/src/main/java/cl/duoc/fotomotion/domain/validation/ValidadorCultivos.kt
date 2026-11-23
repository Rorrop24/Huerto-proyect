package cl.duoc.fotomotion.domain.validation

import cl.duoc.fotomotion.domain.model.Sector

data class ErroresCultivo(
    val nombre: String = "",
    val sector: String = "",
    val fechaSiembra: String = "",
    val fechaTrasplante: String = "",
    val fechaCosecha: String = "",
    val riego: String = ""
) {
    val esValido: Boolean get() = listOf(nombre, sector, fechaSiembra, fechaTrasplante, fechaCosecha, riego).all { error -> error.isEmpty() }
}

// Reglas del formulario de cultivos.
object ValidadorCultivos {
    fun validar(nombre: String, sectorElegido: Int?, sectores: List<Sector>, fechaSiembra: String,
        fechaTrasplante: String, fechaCosecha: String, riego: String) = ErroresCultivo(
        nombre = ValidacionesHuerto.revisarTexto(nombre, 3, 40, "Escribe el nombre del cultivo."),
        sector = ValidacionesHuerto.revisarSector(sectorElegido, sectores, "Selecciona un sector."),
        fechaSiembra = ValidacionesHuerto.revisarFecha(fechaSiembra),
        fechaTrasplante = ValidacionesHuerto.revisarFechaOpcional(fechaTrasplante).ifEmpty {
            ValidacionesHuerto.revisarOrden(fechaSiembra, fechaTrasplante, "El trasplante no puede ser antes de la siembra.")
        },
        fechaCosecha = ValidacionesHuerto.revisarFechaOpcional(fechaCosecha).ifEmpty {
            ValidacionesHuerto.revisarOrden(fechaSiembra, fechaCosecha, "La cosecha no puede ser antes de la siembra.")
        },
        riego = ValidacionesHuerto.revisarNumero(riego, 1, 30, "Indica cada cuántos días se riega.")
    )
}
