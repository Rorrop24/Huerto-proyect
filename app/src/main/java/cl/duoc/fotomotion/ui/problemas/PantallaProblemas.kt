package cl.duoc.fotomotion.ui.problemas

import androidx.compose.animation.AnimatedVisibility
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
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.ProblemaHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun PantallaProblemas(controlProblemas: ProblemasViewModel, cuentaActual: CuentaHuerto) {
    val datosPantalla by controlProblemas.estadoProblemas.collectAsState()
    val permiteSeguimiento = cuentaActual.tipoCuenta == TipoCuenta.DOCENTE
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()

    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { AvisoHuerto(datosPantalla.avisoProblemas) }
        val problemaEnSeguimiento = datosPantalla.problemaEnSeguimiento
        if (permiteSeguimiento && problemaEnSeguimiento != null) item {
            FormularioSeguimiento(datosPantalla, problemaEnSeguimiento, controlProblemas, cuentaActual)
        }
        item { FormularioReporte(datosPantalla, controlProblemas, cuentaActual) }
        item { Text("Problemas reportados", style = MaterialTheme.typography.titleLarge) }
        if (datosPantalla.listaProblemas.isEmpty()) item { Text("No hay problemas reportados.") }
        items(datosPantalla.listaProblemas, key = { problema -> problema.codigoProblema }) { problema ->
            val nombreSector = datosPantalla.sectoresDisponibles.find { parcela -> parcela.codigoParcela == problema.parcelaAfectada }?.tituloParcela ?: "Sector no disponible"
            val nombreCultivo = datosPantalla.cultivosDisponibles.find { siembra -> siembra.codigoSiembra == problema.siembraAfectada }?.tituloSiembra
            TarjetaProblema(problema, nombreSector, nombreCultivo, permiteSeguimiento, habilitado = !datosPantalla.guardandoProblema,
                alSeguir = {
                    controlProblemas.iniciarSeguimiento(problema)
                    accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                })
        }
    }
}

// Formulario para reportar un problema detectado en el huerto.
@Composable
private fun FormularioReporte(datosPantalla: EstadoProblemas, controlProblemas: ProblemasViewModel, cuentaActual: CuentaHuerto) {
    val habilitado = !datosPantalla.guardandoProblema
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Reportar un problema", style = MaterialTheme.typography.titleMedium)
            ElegirSector(datosPantalla.sectoresDisponibles, datosPantalla.sectorElegido, datosPantalla.errorSector,
                controlProblemas::elegirSector, habilitado)
            if (datosPantalla.sectorElegido != null) ElegirCultivo(
                datosPantalla.cultivosDisponibles.filter { siembra -> siembra.parcelaAsociada == datosPantalla.sectorElegido },
                datosPantalla.cultivoElegido, datosPantalla.errorCultivo, controlProblemas::elegirCultivo, habilitado)
            ElegirOpcion("Tipo de problema", TipoProblema.entries, datosPantalla.tipoElegido, { tipo -> tipo.etiqueta },
                controlProblemas::elegirTipo, habilitado)
            if (datosPantalla.errorTipo.isNotEmpty()) ErrorHuerto(datosPantalla.errorTipo, "Error de tipo de problema")
            CampoHuerto(datosPantalla.textoDescripcion, "Descripción del problema", datosPantalla.errorDescripcion,
                controlProblemas::escribirDescripcion, habilitado)
            Text("Foto opcional: solo plantas, cultivos o herramientas, sin personas.", style = MaterialTheme.typography.bodySmall)
            AnimatedVisibility(visible = datosPantalla.direccionFoto.isNotBlank()) {
                FotoHuerto(datosPantalla.direccionFoto)
            }
            AdjuntarFoto(controlProblemas::adjuntarFoto, controlProblemas::informarProblema, habilitado)
            Button(onClick = { controlProblemas.reportarProblema(cuentaActual) }, enabled = habilitado) { Text("Enviar reporte") }
        }
    }
}

// Formulario del docente para cambiar el estado de un reporte y dejar un comentario.
@Composable
private fun FormularioSeguimiento(datosPantalla: EstadoProblemas, problema: ProblemaHuerto,
    controlProblemas: ProblemasViewModel, cuentaActual: CuentaHuerto) {
    val habilitado = !datosPantalla.guardandoProblema
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Seguimiento: ${TipoProblema.desde(problema.claseProblema).etiqueta}", style = MaterialTheme.typography.titleMedium)
            Text(problema.detalleProblema)
            ElegirOpcion("Estado del reporte", EstadoProblema.entries, datosPantalla.estadoSeguimiento,
                { estado -> estado.etiqueta }, controlProblemas::elegirEstadoSeguimiento, habilitado)
            CampoHuerto(datosPantalla.textoSeguimiento, "Comentario de seguimiento (opcional)", datosPantalla.errorSeguimiento,
                controlProblemas::escribirSeguimiento, habilitado)
            Button(onClick = { controlProblemas.guardarSeguimiento(cuentaActual) }, enabled = habilitado) { Text("Guardar seguimiento") }
            TextButton(onClick = controlProblemas::cancelarSeguimiento, enabled = habilitado) { Text("Cancelar seguimiento") }
        }
    }
}

@Composable
private fun TarjetaProblema(problema: ProblemaHuerto, nombreSector: String, nombreCultivo: String?,
    permiteSeguimiento: Boolean, habilitado: Boolean, alSeguir: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(TipoProblema.desde(problema.claseProblema).etiqueta, style = MaterialTheme.typography.titleMedium)
            Text("Sector: $nombreSector" + (nombreCultivo?.let { cultivo -> " · Cultivo: $cultivo" } ?: ""))
            Text(problema.detalleProblema)
            Text("Reportado: ${mostrarMomento(problema.momentoReporte)} · Estado: ${EstadoProblema.desde(problema.situacionProblema).etiqueta}")
            if (!problema.imagenProblema.isNullOrBlank()) FotoHuerto(problema.imagenProblema)
            if (!problema.notaSeguimiento.isNullOrBlank()) Text("Docente: ${problema.notaSeguimiento}")
            problema.momentoCierre?.let { momento -> Text("Atendido el: ${mostrarMomento(momento)}") }
            if (permiteSeguimiento) TextButton(onClick = alSeguir, enabled = habilitado) { Text("Dar seguimiento") }
        }
    }
}
