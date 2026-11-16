package cl.duoc.fotomotion.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import cl.duoc.fotomotion.model.*
import cl.duoc.fotomotion.viewmodel.*
import cl.duoc.fotomotion.ui.components.*

@Composable
fun PantallaSiembras(controlSiembras: SiembrasViewModel, cuentaActual: CuentaHuerto) {
    val datosPantalla by controlSiembras.estadoSiembras.collectAsState()
    val permiteCambios = cuentaActual.tipoCuenta == TipoCuenta.DOCENTE
    var siembraPorBorrar by remember { mutableStateOf<SiembraHuerto?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { AvisoHuerto(datosPantalla.avisoSiembras) }
        if (permiteCambios) item {
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp)) {
                    Text(if (datosPantalla.siembraEnEdicion == null) "Nuevo cultivo" else "Editar cultivo", style = MaterialTheme.typography.titleMedium)
                    CampoHuerto(datosPantalla.textoSiembra, "Nombre del cultivo", datosPantalla.errorSiembra,
                        controlSiembras::escribirSiembra, !datosPantalla.guardandoSiembra)
                    ElegirSector(datosPantalla.opcionesParcela, datosPantalla.parcelaElegida, datosPantalla.errorSeleccion,
                        controlSiembras::elegirParcela, !datosPantalla.guardandoSiembra)
                    Button(onClick = controlSiembras::confirmarSiembra, enabled = !datosPantalla.guardandoSiembra) { Text("Guardar cultivo") }
                    if (datosPantalla.siembraEnEdicion != null) TextButton(onClick = controlSiembras::limpiarSiembra, enabled = !datosPantalla.guardandoSiembra) { Text("Cancelar edición") }
                }
            }
        }
        item { Text("Cultivos registrados", style = MaterialTheme.typography.titleLarge) }
        if (datosPantalla.listaSiembras.isEmpty()) item { Text("Todavía no hay cultivos.") }
        items(datosPantalla.listaSiembras, key = { siembra -> siembra.codigoSiembra }) { siembra ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(siembra.tituloSiembra, style = MaterialTheme.typography.titleMedium)
                    val nombreSector = datosPantalla.opcionesParcela.find { parcela -> parcela.codigoParcela == siembra.parcelaAsociada }?.tituloParcela ?: "Sector no disponible"
                    Text("Sector: $nombreSector")
                    Text("Siembra: ${siembra.diaSiembra} · ${siembra.situacionSiembra}")
                    if (permiteCambios) Row {
                        TextButton(onClick = {
                            controlSiembras.prepararSiembra(siembra)
                            accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                        }, enabled = !datosPantalla.guardandoSiembra) { Text("Editar cultivo") }
                        TextButton(onClick = { siembraPorBorrar = siembra }, enabled = !datosPantalla.guardandoSiembra) { Text("Borrar cultivo") }
                    }
                }
            }
        }
    }
    siembraPorBorrar?.let { siembra ->
        ConfirmarBorrado(siembra.tituloSiembra, alBorrar = {
            controlSiembras.eliminarSiembra(siembra)
            siembraPorBorrar = null
        }, alCancelar = { siembraPorBorrar = null })
    }
}
