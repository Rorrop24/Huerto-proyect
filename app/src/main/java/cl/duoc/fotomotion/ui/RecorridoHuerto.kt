package cl.duoc.fotomotion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.duoc.fotomotion.viewmodel.*
import cl.duoc.fotomotion.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorridoHuerto() {
    val controlAcceso: AccesoViewModel = viewModel()
    val controlParcelas: ParcelasViewModel = viewModel()
    val controlSiembras: SiembrasViewModel = viewModel()
    val controlLabores: LaboresViewModel = viewModel()
    val controlLecturas: LecturasViewModel = viewModel()
    val cuentaActual by controlAcceso.cuentaActiva.collectAsState()
    val navegador = rememberNavController()
    val entradaActual by navegador.currentBackStackEntryAsState()
    val rutaVisible = entradaActual?.destination?.route ?: "acceso"
    val rutasMenu = listOf("parcelas", "siembras", "labores", "lecturas")

    // Al reiniciar el proceso se vuelve a elegir una cuenta de demostración.
    LaunchedEffect(cuentaActual) {
        if (cuentaActual == null && rutaVisible != "acceso") {
            navegador.navigate("acceso") { popUpTo(navegador.graph.id) { inclusive = true } }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(when (rutaVisible) {
                "parcelas" -> "Sectores del huerto"
                "siembras" -> "Cultivos del huerto"
                "labores" -> "Tareas del huerto"
                "lecturas" -> "Lecturas de ejemplo"
                else -> "Huerto Escolar"
            }) }, actions = {
                if (cuentaActual != null) TextButton(onClick = {
                    controlParcelas.limpiarParcela()
                    controlSiembras.limpiarSiembra()
                    controlLabores.limpiarLabor()
                    controlAcceso.salirDeCuenta()
                }) { Text("Salir") }
            })
        },
        bottomBar = {
            if (cuentaActual != null && rutaVisible != "acceso") NavigationBar {
                rutasMenu.forEach { rutaMenu ->
                    val tituloMenu = when (rutaMenu) {
                        "parcelas" -> "Sectores"
                        "siembras" -> "Cultivos"
                        "labores" -> "Tareas"
                        else -> "Lecturas"
                    }
                    NavigationBarItem(selected = rutaVisible == rutaMenu,
                        onClick = {
                            navegador.navigate(rutaMenu) {
                                popUpTo("parcelas")
                                launchSingleTop = true
                            }
                        }, label = { Text(tituloMenu) }, icon = {
                            Icon(when (rutaMenu) {
                                "parcelas" -> Icons.Default.Home
                                "siembras" -> Icons.AutoMirrored.Filled.List
                                "labores" -> Icons.Default.Build
                                else -> Icons.Default.Warning
                            }, contentDescription = tituloMenu)
                        })
                }
            }
        }
    ) { espacioPantalla ->
        NavHost(navController = navegador, startDestination = "acceso",
            modifier = Modifier.fillMaxSize().padding(espacioPantalla).consumeWindowInsets(espacioPantalla).imePadding()) {
            composable("acceso") {
                PantallaAcceso(controlAcceso) { cuentaElegida ->
                    controlAcceso.entrarConCuenta(cuentaElegida)
                    navegador.navigate("parcelas") { popUpTo("acceso") { inclusive = true } }
                }
            }
            composable("parcelas") { cuentaActual?.let { cuenta -> PantallaParcelas(controlParcelas, cuenta) } }
            composable("siembras") { cuentaActual?.let { cuenta -> PantallaSiembras(controlSiembras, cuenta) } }
            composable("labores") { cuentaActual?.let { cuenta -> PantallaLabores(controlLabores, cuenta) } }
            composable("lecturas") { if (cuentaActual != null) PantallaLecturas(controlLecturas) }
        }
    }
}
