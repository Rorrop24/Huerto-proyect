package cl.duoc.fotomotion.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.duoc.fotomotion.ui.FabricaViewModels
import cl.duoc.fotomotion.ui.acceso.AccesoViewModel
import cl.duoc.fotomotion.ui.acceso.PantallaAcceso
import cl.duoc.fotomotion.ui.alertas.AlertasViewModel
import cl.duoc.fotomotion.ui.alertas.PantallaAlertas
import cl.duoc.fotomotion.ui.inicio.PantallaInicio
import cl.duoc.fotomotion.ui.tareas.TareasViewModel
import cl.duoc.fotomotion.ui.tareas.PantallaTareas
import cl.duoc.fotomotion.ui.lecturas.LecturasViewModel
import cl.duoc.fotomotion.ui.lecturas.PantallaLecturas
import cl.duoc.fotomotion.ui.sectores.PantallaSectores
import cl.duoc.fotomotion.ui.sectores.SectoresViewModel
import cl.duoc.fotomotion.ui.problemas.PantallaProblemas
import cl.duoc.fotomotion.ui.problemas.ProblemasViewModel
import cl.duoc.fotomotion.ui.cultivos.PantallaCultivos
import cl.duoc.fotomotion.ui.cultivos.CultivosViewModel

// Punto de entrada de la interfaz: crea los ViewModels, decide la pantalla según la sesión y arma las barras.
@Composable
fun RecorridoHuerto() {
    val accesoViewModel: AccesoViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val sectoresViewModel: SectoresViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val cultivosViewModel: CultivosViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val tareasViewModel: TareasViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val lecturasViewModel: LecturasViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val problemasViewModel: ProblemasViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val alertasViewModel: AlertasViewModel = viewModel(factory = FabricaViewModels.fabrica)
    val accesoUiState by accesoViewModel.uiState.collectAsState()
    val alertasUiState by alertasViewModel.uiState.collectAsState()
    val cuentaActual = accesoUiState.cuentaActiva
    val navegador = rememberNavController()
    val entradaActual by navegador.currentBackStackEntryAsState()
    val rutaVisible = entradaActual?.destination?.route ?: RutasHuerto.ACCESO

    // Al iniciar sesión se entra al inicio; al cerrarla (o si se pierde la sesión) se vuelve al acceso.
    // Se lee la ruta actual del navegador (no la de la composición) para no moverse al girar la pantalla.
    LaunchedEffect(cuentaActual) {
        alertasViewModel.mostrarPara(cuentaActual)
        val rutaActual = navegador.currentDestination?.route
        if (cuentaActual != null && rutaActual == RutasHuerto.ACCESO) {
            navegador.navigate(RutasHuerto.INICIO) { popUpTo(RutasHuerto.ACCESO) { inclusive = true } }
        }
        if (cuentaActual == null && rutaActual != null && rutaActual != RutasHuerto.ACCESO) {
            navegador.navigate(RutasHuerto.ACCESO) { popUpTo(navegador.graph.id) { inclusive = true } }
        }
    }
    Scaffold(
        topBar = {
            BarraSuperiorHuerto(rutaVisible, cuentaActual, alertasUiState.cantidad,
                alAbrirAlertas = { navegador.navigate(RutasHuerto.ALERTAS) { launchSingleTop = true } },
                alSalir = {
                    // Cerrar sesión deja todos los formularios vacíos para el siguiente usuario.
                    sectoresViewModel.limpiarFormulario()
                    cultivosViewModel.limpiarFormulario()
                    tareasViewModel.limpiarFormulario()
                    problemasViewModel.limpiarFormulario()
                    accesoViewModel.salirDeCuenta()
                })
        },
        bottomBar = {
            if (cuentaActual != null && rutaVisible != RutasHuerto.ACCESO) {
                BarraInferiorHuerto(rutaVisible) { rutaMenu ->
                    navegador.navigate(rutaMenu) {
                        popUpTo(RutasHuerto.INICIO)
                        launchSingleTop = true
                    }
                }
            }
        }
    ) { espacioPantalla ->
        NavHost(navController = navegador, startDestination = RutasHuerto.ACCESO,
            modifier = Modifier.fillMaxSize().padding(espacioPantalla).consumeWindowInsets(espacioPantalla).imePadding(),
            enterTransition = { fadeIn(tween(220)) }, exitTransition = { fadeOut(tween(180)) }) {
            composable(RutasHuerto.ACCESO) { PantallaAcceso(accesoViewModel) }
            composable(RutasHuerto.INICIO) {
                cuentaActual?.let { cuenta ->
                    PantallaInicio(cuenta, alertasUiState.cantidad) { ruta ->
                        navegador.navigate(ruta) { launchSingleTop = true }
                    }
                }
            }
            composable(RutasHuerto.SECTORES) { cuentaActual?.let { cuenta -> PantallaSectores(sectoresViewModel, cuenta) } }
            composable(RutasHuerto.CULTIVOS) { cuentaActual?.let { cuenta -> PantallaCultivos(cultivosViewModel, cuenta) } }
            composable(RutasHuerto.TAREAS) { cuentaActual?.let { cuenta -> PantallaTareas(tareasViewModel, cuenta) } }
            composable(RutasHuerto.PROBLEMAS) { cuentaActual?.let { cuenta -> PantallaProblemas(problemasViewModel, cuenta) } }
            composable(RutasHuerto.LECTURAS) { if (cuentaActual != null) PantallaLecturas(lecturasViewModel) }
            composable(RutasHuerto.ALERTAS) { if (cuentaActual != null) PantallaAlertas(alertasViewModel) }
        }
    }
}
