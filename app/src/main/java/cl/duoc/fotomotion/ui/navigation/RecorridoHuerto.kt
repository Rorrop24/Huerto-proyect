package cl.duoc.fotomotion.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.duoc.fotomotion.ui.acceso.AccesoViewModel
import cl.duoc.fotomotion.ui.acceso.PantallaAcceso
import cl.duoc.fotomotion.ui.labores.LaboresViewModel
import cl.duoc.fotomotion.ui.labores.PantallaLabores
import cl.duoc.fotomotion.ui.lecturas.LecturasViewModel
import cl.duoc.fotomotion.ui.lecturas.PantallaLecturas
import cl.duoc.fotomotion.ui.parcelas.PantallaParcelas
import cl.duoc.fotomotion.ui.parcelas.ParcelasViewModel
import cl.duoc.fotomotion.ui.siembras.PantallaSiembras
import cl.duoc.fotomotion.ui.siembras.SiembrasViewModel

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
    val rutaVisible = entradaActual?.destination?.route ?: RutasHuerto.ACCESO

    // Al reiniciar el proceso se vuelve a elegir una cuenta de demostración.
    LaunchedEffect(cuentaActual) {
        if (cuentaActual == null && rutaVisible != RutasHuerto.ACCESO) {
            navegador.navigate(RutasHuerto.ACCESO) { popUpTo(navegador.graph.id) { inclusive = true } }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(when (rutaVisible) {
                RutasHuerto.PARCELAS -> "Sectores del huerto"
                RutasHuerto.SIEMBRAS -> "Cultivos del huerto"
                RutasHuerto.LABORES -> "Tareas del huerto"
                RutasHuerto.LECTURAS -> "Lecturas de ejemplo"
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
            if (cuentaActual != null && rutaVisible != RutasHuerto.ACCESO) NavigationBar {
                RutasHuerto.menu.forEach { rutaMenu ->
                    val tituloMenu = when (rutaMenu) {
                        RutasHuerto.PARCELAS -> "Sectores"
                        RutasHuerto.SIEMBRAS -> "Cultivos"
                        RutasHuerto.LABORES -> "Tareas"
                        else -> "Lecturas"
                    }
                    NavigationBarItem(selected = rutaVisible == rutaMenu,
                        onClick = {
                            navegador.navigate(rutaMenu) {
                                popUpTo(RutasHuerto.PARCELAS)
                                launchSingleTop = true
                            }
                        }, label = { Text(tituloMenu) }, icon = {
                            Icon(when (rutaMenu) {
                                RutasHuerto.PARCELAS -> Icons.Default.Home
                                RutasHuerto.SIEMBRAS -> Icons.AutoMirrored.Filled.List
                                RutasHuerto.LABORES -> Icons.Default.Build
                                else -> Icons.Default.Warning
                            }, contentDescription = tituloMenu)
                        })
                }
            }
        }
    ) { espacioPantalla ->
        NavHost(navController = navegador, startDestination = RutasHuerto.ACCESO,
            modifier = Modifier.fillMaxSize().padding(espacioPantalla).consumeWindowInsets(espacioPantalla).imePadding()) {
            composable(RutasHuerto.ACCESO) {
                PantallaAcceso(controlAcceso) { cuentaElegida ->
                    controlAcceso.entrarConCuenta(cuentaElegida)
                    navegador.navigate(RutasHuerto.PARCELAS) { popUpTo(RutasHuerto.ACCESO) { inclusive = true } }
                }
            }
            composable(RutasHuerto.PARCELAS) { cuentaActual?.let { cuenta -> PantallaParcelas(controlParcelas, cuenta) } }
            composable(RutasHuerto.SIEMBRAS) { cuentaActual?.let { cuenta -> PantallaSiembras(controlSiembras, cuenta) } }
            composable(RutasHuerto.LABORES) { cuentaActual?.let { cuenta -> PantallaLabores(controlLabores, cuenta) } }
            composable(RutasHuerto.LECTURAS) { if (cuentaActual != null) PantallaLecturas(controlLecturas) }
        }
    }
}
