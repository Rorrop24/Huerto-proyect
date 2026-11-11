package cl.duoc.fotomotion.ui.siembras

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.CuentaHuerto
import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun PantallaSiembras(controlSiembras: SiembrasViewModel, cuentaActual: CuentaHuerto) {
    val datosPantalla by controlSiembras.estadoSiembras.collectAsState()
    val permiteCambios = cuentaActual.tipoCuenta == TipoCuenta.DOCENTE
    var siembraPorBorrar by remember { mutableStateOf<SiembraHuerto?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { AvisoHuerto(datosPantalla.avisoSiembras) }
        if (permiteCambios) item { FormularioSiembra(datosPantalla, controlSiembras) }
        item { Text("Cultivos registrados", style = MaterialTheme.typography.titleLarge) }
        if (datosPantalla.listaSiembras.isEmpty()) item { Text("Todavía no hay cultivos.") }
        items(datosPantalla.listaSiembras, key = { siembra -> siembra.codigoSiembra }) { siembra ->
            val nombreSector = datosPantalla.opcionesParcela.find { parcela -> parcela.codigoParcela == siembra.parcelaAsociada }?.tituloParcela ?: "Sector no disponible"
            TarjetaSiembra(siembra, nombreSector, permiteCambios, habilitado = !datosPantalla.guardandoSiembra,
                alEditar = {
                    controlSiembras.prepararSiembra(siembra)
                    accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                },
                alBorrar = { siembraPorBorrar = siembra })
        }
    }
    siembraPorBorrar?.let { siembra ->
        ConfirmarBorrado(siembra.tituloSiembra, alBorrar = {
            controlSiembras.eliminarSiembra(siembra)
            siembraPorBorrar = null
        }, alCancelar = { siembraPorBorrar = null })
    }
}

// Formulario del docente para crear o editar un cultivo.
@Composable
private fun FormularioSiembra(datosPantalla: EstadoSiembras, controlSiembras: SiembrasViewModel) {
    val habilitado = !datosPantalla.guardandoSiembra
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp)) {
            Text(if (datosPantalla.siembraEnEdicion == null) "Nuevo cultivo" else "Editar cultivo", style = MaterialTheme.typography.titleMedium)
            CampoHuerto(datosPantalla.textoSiembra, "Nombre del cultivo", datosPantalla.errorSiembra,
                controlSiembras::escribirSiembra, habilitado)
            ElegirSector(datosPantalla.opcionesParcela, datosPantalla.parcelaElegida, datosPantalla.errorSeleccion,
                controlSiembras::elegirParcela, habilitado)
            ElegirOpcion("Estado del cultivo", EstadoCultivo.entries, datosPantalla.estadoCultivo,
                { estado -> estado.etiqueta }, controlSiembras::elegirEstado, habilitado)
            CampoHuerto(datosPantalla.textoFechaSiembra, "Fecha de siembra (AAAA-MM-DD)", datosPantalla.errorFechaSiembra,
                controlSiembras::escribirFechaSiembra, habilitado)
            CampoHuerto(datosPantalla.textoFechaTraslado, "Fecha de trasplante (opcional)", datosPantalla.errorFechaTraslado,
                controlSiembras::escribirFechaTraslado, habilitado)
            CampoHuerto(datosPantalla.textoFechaCosecha, "Cosecha estimada (opcional)", datosPantalla.errorFechaCosecha,
                controlSiembras::escribirFechaCosecha, habilitado)
            CampoHuerto(datosPantalla.textoRiego, "Riego cada (días)", datosPantalla.errorRiego,
                controlSiembras::escribirRiego, habilitado)
            Button(onClick = controlSiembras::confirmarSiembra, enabled = habilitado) { Text("Guardar cultivo") }
            if (datosPantalla.siembraEnEdicion != null) TextButton(onClick = controlSiembras::limpiarSiembra, enabled = habilitado) { Text("Cancelar edición") }
        }
    }
}

@Composable
private fun TarjetaSiembra(siembra: SiembraHuerto, nombreSector: String, permiteCambios: Boolean, habilitado: Boolean,
    alEditar: () -> Unit, alBorrar: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(siembra.tituloSiembra, style = MaterialTheme.typography.titleMedium)
            Text("Sector: $nombreSector · ${EstadoCultivo.desde(siembra.situacionSiembra).etiqueta}")
            val unidadRiego = if (siembra.intervaloRiego == 1) "día" else "días"
            Text("Siembra: ${siembra.diaSiembra ?: "sin fecha"} · Riego cada ${siembra.intervaloRiego} $unidadRiego")
            siembra.diaTraslado?.let { fecha -> Text("Trasplante: $fecha") }
            siembra.diaCosecha?.let { fecha -> Text("Cosecha estimada: $fecha") }
            if (permiteCambios) Row {
                TextButton(onClick = alEditar, enabled = habilitado) { Text("Editar cultivo") }
                TextButton(onClick = alBorrar, enabled = habilitado) { Text("Borrar cultivo") }
            }
        }
    }
}
