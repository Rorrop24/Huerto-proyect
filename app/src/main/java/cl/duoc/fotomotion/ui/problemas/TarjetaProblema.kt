package cl.duoc.fotomotion.ui.problemas

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.ui.components.*

@Composable
internal fun TarjetaProblema(problema: Problema, nombreSector: String, nombreCultivo: String?,
    permiteSeguimiento: Boolean, habilitado: Boolean, alSeguir: () -> Unit) {
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(problema.tipo.etiqueta, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                EtiquetaEstado(problema.estado.etiqueta, problema.estado.tono())
            }
            Text("Sector: $nombreSector" + (nombreCultivo?.let { cultivo -> " · Cultivo: $cultivo" } ?: ""))
            Text(problema.descripcion)
            Text("Reportado: ${mostrarMomento(problema.fechaReporte)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!problema.fotografia.isNullOrBlank()) FotoHuerto(problema.fotografia)
            if (!problema.comentarioDocente.isNullOrBlank()) Text("Docente: ${problema.comentarioDocente}")
            problema.fechaCierre?.let { momento -> Text("Atendido el: ${mostrarMomento(momento)}") }
            if (permiteSeguimiento) TextButton(onClick = alSeguir, enabled = habilitado) { Text("Dar seguimiento") }
        }
    }
}
