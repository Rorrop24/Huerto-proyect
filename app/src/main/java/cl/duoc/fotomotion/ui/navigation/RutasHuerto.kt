package cl.duoc.fotomotion.ui.navigation

object RutasHuerto {
    const val ACCESO = "acceso"
    const val INICIO = "inicio"
    const val SECTORES = "sectores"
    const val CULTIVOS = "cultivos"
    const val TAREAS = "tareas"
    const val PROBLEMAS = "problemas"
    const val LECTURAS = "lecturas"
    const val ALERTAS = "alertas"

    // Destinos de la barra inferior, en orden. Lecturas y recordatorios se abren desde el inicio o la campana.
    val menu = listOf(INICIO, SECTORES, CULTIVOS, TAREAS, PROBLEMAS)
}
