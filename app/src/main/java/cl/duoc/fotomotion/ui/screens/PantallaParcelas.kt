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
fun PantallaParcelas(controlParcelas: ParcelasViewModel, cuentaActual: CuentaHuerto) {
    val datosPantalla by controlParcelas.estadoParcelas.collectAsState()
    val permiteCambios = cuentaActual.tipoCuenta == TipoCuenta.DOCENTE
    var parcelaPorBorrar by remember { mutableStateOf<ParcelaHuerto?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("${cuentaActual.nombreCuenta} · ${datosPantalla.listaParcelas.size} sectores") }
        item { AvisoHuerto(datosPantalla.avisoParcelas) }
        if (permiteCambios) item {
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp)) {
                    Text(if (datosPantalla.parcelaEnEdicion == null) "Nuevo sector" else "Editar sector", style = MaterialTheme.typography.titleMedium)
                    CampoHuerto(datosPantalla.textoParcela, "Nombre del sector", datosPantalla.errorParcela,
                        controlParcelas::escribirParcela, !datosPantalla.guardandoParcela)
                    CampoHuerto(datosPantalla.textoLugar, "Ubicación", datosPantalla.errorLugar,
                        controlParcelas::escribirLugar, !datosPantalla.guardandoParcela)
                    Button(onClick = controlParcelas::confirmarParcela, enabled = !datosPantalla.guardandoParcela) { Text("Guardar sector") }
                    if (datosPantalla.parcelaEnEdicion != null) TextButton(onClick = controlParcelas::limpiarParcela, enabled = !datosPantalla.guardandoParcela) { Text("Cancelar edición") }
                }
            }
        }
        item { Text("Sectores registrados", style = MaterialTheme.typography.titleLarge) }
        if (datosPantalla.listaParcelas.isEmpty()) item { Text("Todavía no hay sectores. El docente puede crear el primero.") }
        items(datosPantalla.listaParcelas, key = { parcela -> parcela.codigoParcela }) { parcela ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(parcela.tituloParcela, style = MaterialTheme.typography.titleMedium)
                    Text("Ubicación: ${parcela.lugarParcela}")
                    if (permiteCambios) Row {
                        TextButton(onClick = {
                            controlParcelas.prepararParcela(parcela)
                            accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                        }, enabled = !datosPantalla.guardandoParcela) { Text("Editar sector") }
                        TextButton(onClick = { parcelaPorBorrar = parcela }, enabled = !datosPantalla.guardandoParcela) { Text("Borrar sector") }
                    }
                }
            }
        }
    }
    parcelaPorBorrar?.let { parcela ->
        ConfirmarBorrado(parcela.tituloParcela, alBorrar = {
            controlParcelas.eliminarParcela(parcela)
            parcelaPorBorrar = null
        }, alCancelar = { parcelaPorBorrar = null })
    }
}
