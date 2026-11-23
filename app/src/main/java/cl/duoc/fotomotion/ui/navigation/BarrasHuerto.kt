package cl.duoc.fotomotion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.fotomotion.domain.model.Cuenta

// Barra superior: título de la pantalla actual, campana de recordatorios y botón "Salir".
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorHuerto(rutaVisible: String, cuentaActual: Cuenta?, cantidadAlertas: Int,
    alAbrirAlertas: () -> Unit, alSalir: () -> Unit) {
    TopAppBar(title = { Text(tituloDePantalla(rutaVisible, cuentaActual)) }, actions = {
        if (cuentaActual != null) {
            IconButton(onClick = alAbrirAlertas) {
                BadgedBox(badge = { if (cantidadAlertas > 0) Badge { Text("$cantidadAlertas") } }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Recordatorios")
                }
            }
            TextButton(onClick = alSalir) { Text("Salir") }
        }
    })
}

// Barra inferior con las pestañas de RutasHuerto.menu.
@Composable
fun BarraInferiorHuerto(rutaVisible: String, alNavegar: (String) -> Unit) {
    NavigationBar {
        RutasHuerto.menu.forEach { rutaMenu ->
            val tituloMenu = tituloDePestana(rutaMenu)
            NavigationBarItem(selected = rutaVisible == rutaMenu, onClick = { alNavegar(rutaMenu) },
                label = { Text(tituloMenu) },
                icon = { Icon(iconoDePestana(rutaMenu), contentDescription = tituloMenu) })
        }
    }
}

private fun tituloDePantalla(ruta: String, cuentaActual: Cuenta?): String = when (ruta) {
    RutasHuerto.SECTORES -> "Sectores del huerto"
    RutasHuerto.CULTIVOS -> "Cultivos del huerto"
    RutasHuerto.TAREAS -> if (cuentaActual?.esDocente == true) "Tareas del huerto" else "Mis tareas"
    RutasHuerto.PROBLEMAS -> "Problemas del huerto"
    RutasHuerto.LECTURAS -> "Lecturas de ejemplo"
    RutasHuerto.ALERTAS -> "Recordatorios"
    else -> "Huerto Escolar"
}

private fun tituloDePestana(ruta: String): String = when (ruta) {
    RutasHuerto.INICIO -> "Inicio"
    RutasHuerto.SECTORES -> "Sectores"
    RutasHuerto.CULTIVOS -> "Cultivos"
    RutasHuerto.TAREAS -> "Tareas"
    else -> "Problemas"
}

private fun iconoDePestana(ruta: String): ImageVector = when (ruta) {
    RutasHuerto.INICIO -> Icons.Default.Home
    RutasHuerto.SECTORES -> Icons.Default.Place
    RutasHuerto.CULTIVOS -> Icons.AutoMirrored.Filled.List
    RutasHuerto.TAREAS -> Icons.Default.Build
    else -> Icons.Default.Warning
}
