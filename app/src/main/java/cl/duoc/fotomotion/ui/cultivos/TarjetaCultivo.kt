package cl.duoc.fotomotion.ui.cultivos

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.ui.components.*

@Composable
internal fun TarjetaCultivo(cultivo: Cultivo, nombreSector: String, permiteCambios: Boolean, habilitado: Boolean,
    alEditar: () -> Unit, alBorrar: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cultivo.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                EtiquetaEstado(cultivo.estado.etiqueta, cultivo.estado.tono())
            }
            Text("Sector: $nombreSector", color = MaterialTheme.colorScheme.onSurfaceVariant)
            val unidadRiego = if (cultivo.frecuenciaRiegoDias == 1) "día" else "días"
            Text("Siembra: ${cultivo.fechaSiembra ?: "sin fecha"} · Riego cada ${cultivo.frecuenciaRiegoDias} $unidadRiego")
            cultivo.fechaTrasplante?.let { fecha -> Text("Trasplante: $fecha") }
            cultivo.fechaCosecha?.let { fecha -> Text("Cosecha estimada: $fecha") }
            if (permiteCambios) Row {
                TextButton(onClick = alEditar, enabled = habilitado) { Text("Editar cultivo") }
                TextButton(onClick = alBorrar, enabled = habilitado) { Text("Borrar cultivo") }
            }
        }
    }
}
