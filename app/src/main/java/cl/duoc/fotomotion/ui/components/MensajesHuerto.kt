package cl.duoc.fotomotion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AvisoHuerto(textoAviso: String) {
    AnimatedVisibility(visible = textoAviso.isNotBlank(), enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        Card(modifier = Modifier.fillMaxWidth().animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Text(textoAviso, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

// Mensaje de error con ícono para datos que no son campos de texto (selecciones, fotos). Aparece y se oculta con animación.
@Composable
fun ErrorHuerto(mensajeError: String, descripcionIcono: String) {
    AnimatedVisibility(visible = mensajeError.isNotEmpty(), enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Warning, contentDescription = descripcionIcono, tint = MaterialTheme.colorScheme.error)
            Text(mensajeError, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ConfirmarBorrado(nombreRegistro: String, alBorrar: () -> Unit, alCancelar: () -> Unit) {
    AlertDialog(onDismissRequest = alCancelar, title = { Text("Eliminar registro") },
        text = { Text("¿Quieres eliminar $nombreRegistro?") },
        confirmButton = { TextButton(onClick = alBorrar) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } })
}
