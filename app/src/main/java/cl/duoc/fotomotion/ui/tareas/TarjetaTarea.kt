package cl.duoc.fotomotion.ui.tareas

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.EstadoTarea
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.estaAtrasada
import cl.duoc.fotomotion.ui.components.*
import java.time.LocalDate

@Composable
internal fun TarjetaTarea(tarea: Tarea, nombreSector: String, nombreCultivo: String?, nombreEstudiante: String,
    esDocente: Boolean, habilitado: Boolean, alRealizar: () -> Unit, alRevisar: (Boolean) -> Unit,
    alEditar: () -> Unit, alBorrar: () -> Unit) {
    val atrasada = tarea.estaAtrasada(LocalDate.now())
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tarea.descripcion, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                EtiquetaEstado(if (atrasada) "Atrasada" else tarea.estado.etiqueta,
                    if (atrasada) TonoEstado.ERROR else tarea.estado.tono())
            }
            Text("Sector: $nombreSector · ${tarea.fechaProgramada}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Tipo: ${tarea.tipo.etiqueta}" + (nombreCultivo?.let { cultivo -> " · Cultivo: $cultivo" } ?: ""))
            if (esDocente) Text("Asignada a: $nombreEstudiante")
            if (!tarea.fotografia.isNullOrBlank()) FotoHuerto(tarea.fotografia)
            if (!tarea.comentarioEstudiante.isNullOrBlank()) Text("Estudiante: ${tarea.comentarioEstudiante}")
            if (!tarea.comentarioDocente.isNullOrBlank()) Text("Docente: ${tarea.comentarioDocente}")
            if (!esDocente && tarea.estado.admiteEntrega) {
                Button(onClick = alRealizar, enabled = habilitado) { Text("Realizar tarea") }
            }
            if (esDocente && tarea.estado == EstadoTarea.PENDIENTE_DE_VALIDACION) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { alRevisar(true) }, enabled = habilitado) { Text("Aprobar") }
                    OutlinedButton(onClick = { alRevisar(false) }, enabled = habilitado) { Text("Rechazar") }
                }
            }
            if (esDocente) Row {
                TextButton(onClick = alEditar, enabled = habilitado) { Text("Editar tarea") }
                TextButton(onClick = alBorrar, enabled = habilitado) { Text("Borrar tarea") }
            }
        }
    }
}
