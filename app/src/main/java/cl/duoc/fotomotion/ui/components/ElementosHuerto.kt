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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cl.duoc.fotomotion.R
import cl.duoc.fotomotion.model.ParcelaHuerto
import androidx.compose.ui.res.painterResource

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

@Composable
fun ElegirSector(listaOpciones: List<ParcelaHuerto>, codigoSeleccionado: Int?,
    mensajeSeleccion: String, alSeleccionar: (Int) -> Unit, habilitado: Boolean = true) {
    Text("Sector asociado", style = MaterialTheme.typography.labelLarge)
    if (listaOpciones.isEmpty()) Text("Primero crea un sector en la pestaña Sectores.")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(listaOpciones, key = { parcela -> parcela.codigoParcela }) { parcela ->
            FilterChip(selected = codigoSeleccionado == parcela.codigoParcela,
                onClick = { alSeleccionar(parcela.codigoParcela) }, enabled = habilitado,
                label = { Text(parcela.tituloParcela) })
        }
    }
    if (mensajeSeleccion.isNotEmpty()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Warning, contentDescription = "Error de selección", tint = MaterialTheme.colorScheme.error)
            Text(mensajeSeleccion, color = MaterialTheme.colorScheme.error)
        }
    }
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
