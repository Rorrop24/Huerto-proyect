package cl.duoc.fotomotion.ui.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.ui.navigation.RutasHuerto

private data class OpcionMenu(val titulo: String, val descripcion: String, val icono: ImageVector, val ruta: String)

// Pantalla de bienvenida con el menú principal según el rol.
@Composable
fun PantallaInicio(cuentaActual: Cuenta, cantidadRecordatorios: Int, alNavegar: (String) -> Unit) {
    val opciones = listOf(
        OpcionMenu("Sectores del huerto", if (cuentaActual.esDocente) "Crear y administrar zonas" else "Ver las zonas del huerto",
            Icons.Default.Place, RutasHuerto.SECTORES),
        OpcionMenu("Cultivos sembrados", "Fechas de siembra, riego y cosecha", Icons.AutoMirrored.Filled.List, RutasHuerto.CULTIVOS),
        OpcionMenu(if (cuentaActual.esDocente) "Tareas y revisiones" else "Mis tareas asignadas",
            if (cuentaActual.esDocente) "Asignar tareas y revisar evidencias" else "Entregar con foto y comentario",
            Icons.Default.Build, RutasHuerto.TAREAS),
        OpcionMenu("Problemas reportados", "Avisar o dar seguimiento a problemas", Icons.Default.Warning, RutasHuerto.PROBLEMAS),
        OpcionMenu("Ver recordatorios", "$cantidadRecordatorios pendientes", Icons.Default.Notifications, RutasHuerto.ALERTAS),
        OpcionMenu("Lecturas simuladas", "Humedad y temperatura de ejemplo", Icons.Default.Info, RutasHuerto.LECTURAS)
    )
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Hola, ${cuentaActual.nombre}", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                Text("Perfil: ${cuentaActual.tipo.etiqueta}", color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text("¿Qué quieres hacer hoy?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(opciones) { opcion ->
            Card(onClick = { alNavegar(opcion.ruta) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(opcion.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(opcion.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
