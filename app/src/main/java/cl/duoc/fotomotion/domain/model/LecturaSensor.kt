package cl.duoc.fotomotion.domain.model

// Lectura simulada de sensores (no viene de equipos físicos).
data class LecturaSensor(val lugar: String, val humedadSuelo: Int, val temperatura: Int, val humedadAmbiente: Int) {
    val humedadBaja: Boolean get() = humedadSuelo < 30
}
