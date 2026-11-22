package cl.duoc.fotomotion.ui.labores

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
import cl.duoc.fotomotion.domain.model.LaborHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.TipoLabor
import cl.duoc.fotomotion.domain.model.estaAtrasada
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PantallaLabores(controlLabores: LaboresViewModel, cuentaActual: CuentaHuerto) {
    val datosPantalla by controlLabores.estadoLabores.collectAsState()
    val permiteRevision = cuentaActual.tipoCuenta == TipoCuenta.DOCENTE
    var laborPorBorrar by remember { mutableStateOf<LaborHuerto?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    val subirAlInicio: () -> Unit = { accionesPantalla.launch { posicionLista.animateScrollToItem(0) } }

    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { AvisoHuerto(datosPantalla.avisoLabores) }
        if (permiteRevision) item { FormularioLabor(datosPantalla, controlLabores, cuentaActual) }
        if (!permiteRevision && datosPantalla.laborEnEntrega != null) item {
            FormularioEntrega(datosPantalla, controlLabores, cuentaActual)
        }
        item { Text("Tareas registradas", style = MaterialTheme.typography.titleLarge) }
        if (datosPantalla.listaLabores.isEmpty()) item { Text("Todavía no hay tareas. Ingresa como docente para crear una.") }
        items(datosPantalla.listaLabores, key = { labor -> labor.codigoLabor }) { labor ->
            val nombreSector = datosPantalla.sectoresDisponibles.find { parcela -> parcela.codigoParcela == labor.parcelaTrabajo }?.tituloParcela ?: "Sector no disponible"
            val nombreCultivo = datosPantalla.cultivosDisponibles.find { siembra -> siembra.codigoSiembra == labor.siembraTrabajo }?.tituloSiembra
            TarjetaLabor(labor, nombreSector, nombreCultivo, permiteRevision, habilitado = !datosPantalla.guardandoLabor,
                alRealizar = {
                    controlLabores.iniciarEntrega(labor)
                    subirAlInicio()
                },
                alRevisar = { aceptarTrabajo -> controlLabores.revisarTrabajo(labor, aceptarTrabajo, cuentaActual) },
                alEditar = {
                    controlLabores.prepararLabor(labor)
                    subirAlInicio()
                },
                alBorrar = { laborPorBorrar = labor })
        }
    }
    laborPorBorrar?.let { labor ->
        ConfirmarBorrado(labor.detalleLabor, alBorrar = {
            controlLabores.eliminarLabor(labor)
            laborPorBorrar = null
        }, alCancelar = { laborPorBorrar = null })
    }
}

// Formulario del docente para crear o editar una tarea.
@Composable
private fun FormularioLabor(datosPantalla: EstadoLabores, controlLabores: LaboresViewModel, cuentaActual: CuentaHuerto) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp)) {
            Text(if (datosPantalla.laborEnEdicion == null) "Nueva tarea" else "Editar tarea", style = MaterialTheme.typography.titleMedium)
            CampoHuerto(datosPantalla.textoDetalle, "Descripción de la tarea", datosPantalla.errorDetalle,
                controlLabores::escribirDetalle, !datosPantalla.guardandoLabor)
            CampoHuerto(datosPantalla.textoFecha, "Fecha (AAAA-MM-DD)", datosPantalla.errorFecha,
                controlLabores::escribirFecha, !datosPantalla.guardandoLabor)
            ElegirOpcion("Tipo de tarea", TipoLabor.entries, datosPantalla.tipoLabor, { tipo -> tipo.etiqueta },
                controlLabores::elegirTipoLabor, !datosPantalla.guardandoLabor)
            ElegirSector(datosPantalla.sectoresDisponibles, datosPantalla.sectorTrabajo, datosPantalla.errorSector,
                controlLabores::elegirSectorTrabajo, !datosPantalla.guardandoLabor)
            if (datosPantalla.sectorTrabajo != null) ElegirCultivo(
                datosPantalla.cultivosDisponibles.filter { siembra -> siembra.parcelaAsociada == datosPantalla.sectorTrabajo },
                datosPantalla.cultivoTrabajo, datosPantalla.errorCultivo, controlLabores::elegirCultivoTrabajo,
                !datosPantalla.guardandoLabor)
            Button(onClick = { controlLabores.confirmarLabor(cuentaActual) }, enabled = !datosPantalla.guardandoLabor) { Text("Guardar tarea") }
            if (datosPantalla.laborEnEdicion != null) TextButton(onClick = controlLabores::limpiarLabor, enabled = !datosPantalla.guardandoLabor) { Text("Cancelar edición") }
        }
    }
}

// Formulario del estudiante para entregar la evidencia de una tarea.
@Composable
private fun FormularioEntrega(datosPantalla: EstadoLabores, controlLabores: LaboresViewModel, cuentaActual: CuentaHuerto) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Entregar: ${datosPantalla.laborEnEntrega?.detalleLabor}", style = MaterialTheme.typography.titleMedium)
            AnimatedVisibility(visible = datosPantalla.direccionFoto.isNotBlank()) {
                FotoHuerto(datosPantalla.direccionFoto)
            }
            AdjuntarFoto(controlLabores::adjuntarImagen, controlLabores::informarLabor, !datosPantalla.guardandoLabor)
            if (datosPantalla.errorFoto.isNotEmpty()) ErrorHuerto(datosPantalla.errorFoto, "Error de fotografía")
            CampoHuerto(datosPantalla.textoComentario, "Comentario del trabajo", datosPantalla.errorComentario,
                controlLabores::escribirComentario, !datosPantalla.guardandoLabor)
            Button(onClick = { controlLabores.entregarLabor(cuentaActual) }, enabled = !datosPantalla.guardandoLabor) { Text("Enviar evidencia") }
            TextButton(onClick = controlLabores::limpiarLabor, enabled = !datosPantalla.guardandoLabor) { Text("Cancelar entrega") }
        }
    }
}

@Composable
private fun TarjetaLabor(labor: LaborHuerto, nombreSector: String, nombreCultivo: String?, permiteRevision: Boolean, habilitado: Boolean,
    alRealizar: () -> Unit, alRevisar: (Boolean) -> Unit, alEditar: () -> Unit, alBorrar: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(labor.detalleLabor, style = MaterialTheme.typography.titleMedium)
            Text("Sector: $nombreSector · ${labor.diaPlanificado}")
            Text("Tipo: ${TipoLabor.desde(labor.claseLabor).etiqueta}" + (nombreCultivo?.let { cultivo -> " · Cultivo: $cultivo" } ?: ""))
            Text("Estado: " + when {
                labor.estaAtrasada(LocalDate.now()) -> "Atrasada"
                labor.situacionLabor == "VALIDADA" -> "Aprobada"
                labor.situacionLabor == "RECHAZADA" -> "Requiere corrección"
                labor.situacionLabor == "PENDIENTE_DE_VALIDACION" -> "En revisión"
                else -> "Pendiente"
            })
            if (!labor.imagenLabor.isNullOrBlank()) FotoHuerto(labor.imagenLabor)
            if (!labor.notaEntrega.isNullOrBlank()) Text("Estudiante: ${labor.notaEntrega}")
            if (!labor.notaRevision.isNullOrBlank()) Text("Docente: ${labor.notaRevision}")
            if (!permiteRevision && (labor.situacionLabor == "PENDIENTE" || labor.situacionLabor == "RECHAZADA")) {
                Button(onClick = alRealizar, enabled = habilitado) { Text("Realizar tarea") }
            }
            if (permiteRevision && labor.situacionLabor == "PENDIENTE_DE_VALIDACION") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { alRevisar(true) }, enabled = habilitado) { Text("Aprobar") }
                    OutlinedButton(onClick = { alRevisar(false) }, enabled = habilitado) { Text("Rechazar") }
                }
            }
            if (permiteRevision) Row {
                TextButton(onClick = alEditar, enabled = habilitado) { Text("Editar tarea") }
                TextButton(onClick = alBorrar, enabled = habilitado) { Text("Borrar tarea") }
            }
        }
    }
}
