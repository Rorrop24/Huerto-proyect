package cl.duoc.fotomotion.ui.navigation

object RutasHuerto {
    const val ACCESO = "acceso"
    const val PARCELAS = "parcelas"
    const val SIEMBRAS = "siembras"
    const val LABORES = "labores"
    const val PROBLEMAS = "problemas"
    const val LECTURAS = "lecturas"
    const val ALERTAS = "alertas"

    // Destinos que aparecen en la barra inferior, en orden. Las alertas se abren desde la barra superior.
    val menu = listOf(PARCELAS, SIEMBRAS, LABORES, PROBLEMAS, LECTURAS)
}
