package cl.duoc.fotomotion.ui.sectores

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.ui.components.*

@Composable
internal fun TarjetaSector(sector: Sector, permiteCambios: Boolean, habilitado: Boolean,
    alEditar: () -> Unit, alBorrar: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(sector.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                EtiquetaEstado(sector.estado.etiqueta, sector.estado.tono())
            }
            Text("Ubicación: ${sector.ubicacion}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (permiteCambios) Row {
                TextButton(onClick = alEditar, enabled = habilitado) { Text("Editar sector") }
                TextButton(onClick = alBorrar, enabled = habilitado) { Text("Borrar sector") }
            }
        }
    }
}
