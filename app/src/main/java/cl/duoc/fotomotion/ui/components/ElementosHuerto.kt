package cl.duoc.fotomotion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cl.duoc.fotomotion.R
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.model.admiteAsignaciones

@Composable
fun CampoHuerto(valorCampo: String, etiquetaCampo: String, mensajeCampo: String,
    alEscribir: (String) -> Unit, habilitado: Boolean = true) {
    OutlinedTextField(value = valorCampo, onValueChange = alEscribir, enabled = habilitado,
        label = { Text(etiquetaCampo) }, singleLine = true, isError = mensajeCampo.isNotEmpty(),
        supportingText = { if (mensajeCampo.isNotEmpty()) Text(mensajeCampo) },
        trailingIcon = {
            if (mensajeCampo.isNotEmpty()) Icon(Icons.Default.Warning, contentDescription = "Error en $etiquetaCampo")
        }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun AvisoHuerto(textoAviso: String) {
    AnimatedVisibility(visible = textoAviso.isNotBlank()) {
        Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            Text(textoAviso, modifier = Modifier.padding(12.dp))
        }
    }
}

// Mensaje de error con ícono para datos que no son campos de texto (selecciones, fotos).
@Composable
fun ErrorHuerto(mensajeError: String, descripcionIcono: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.Warning, contentDescription = descripcionIcono, tint = MaterialTheme.colorScheme.error)
        Text(mensajeError, color = MaterialTheme.colorScheme.error)
    }
}

// Fila de chips para elegir una opción de una lista (estados, tipos, cultivos).
@Composable
fun <T> ElegirOpcion(titulo: String, opciones: List<T>, seleccionada: T?, etiqueta: (T) -> String,
    alElegir: (T) -> Unit, habilitado: Boolean = true) {
    Text(titulo, style = MaterialTheme.typography.labelLarge)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { opcion ->
            FilterChip(selected = seleccionada == opcion, onClick = { alElegir(opcion) }, enabled = habilitado,
                label = { Text(etiqueta(opcion)) })
        }
    }
}

@Composable
fun ElegirSector(listaOpciones: List<ParcelaHuerto>, codigoSeleccionado: Int?,
    mensajeSeleccion: String, alSeleccionar: (Int) -> Unit, habilitado: Boolean = true) {
    val sectoresAsignables = listaOpciones.filter { parcela -> parcela.admiteAsignaciones }
    Text("Sector asociado", style = MaterialTheme.typography.labelLarge)
    if (listaOpciones.isEmpty()) Text("Primero crea un sector en la pestaña Sectores.")
    else if (sectoresAsignables.isEmpty()) Text("No hay sectores disponibles: todos están fuera de uso.")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(sectoresAsignables, key = { parcela -> parcela.codigoParcela }) { parcela ->
            FilterChip(selected = codigoSeleccionado == parcela.codigoParcela,
                onClick = { alSeleccionar(parcela.codigoParcela) }, enabled = habilitado,
                label = { Text(parcela.tituloParcela) })
        }
    }
    if (mensajeSeleccion.isNotEmpty()) ErrorHuerto(mensajeSeleccion, "Error de selección")
}

// Cultivo opcional dentro del sector elegido; "Sin cultivo" deja la relación vacía.
@Composable
fun ElegirCultivo(cultivosDelSector: List<SiembraHuerto>, codigoSeleccionado: Int?, mensajeError: String,
    alSeleccionar: (Int?) -> Unit, habilitado: Boolean = true) {
    ElegirOpcion("Cultivo relacionado (opcional)", listOf<SiembraHuerto?>(null) + cultivosDelSector,
        cultivosDelSector.find { siembra -> siembra.codigoSiembra == codigoSeleccionado },
        { siembra -> siembra?.tituloSiembra ?: "Sin cultivo" }, { siembra -> alSeleccionar(siembra?.codigoSiembra) }, habilitado)
    if (mensajeError.isNotEmpty()) ErrorHuerto(mensajeError, "Error de cultivo")
}

@Composable
fun FotoHuerto(direccionImagen: String) {
    AsyncImage(model = direccionImagen, contentDescription = "Fotografía del trabajo en el huerto",
        error = painterResource(R.drawable.ic_huerto), contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)))
}

@Composable
fun ConfirmarBorrado(nombreRegistro: String, alBorrar: () -> Unit, alCancelar: () -> Unit) {
    AlertDialog(onDismissRequest = alCancelar, title = { Text("Eliminar registro") },
        text = { Text("¿Quieres eliminar $nombreRegistro?") },
        confirmButton = { TextButton(onClick = alBorrar) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } })
}

// Convierte "2026-11-11T10:30:15" en "2026-11-11 10:30" para mostrarlo.
fun mostrarMomento(momento: String): String = momento.replace('T', ' ').take(16)
