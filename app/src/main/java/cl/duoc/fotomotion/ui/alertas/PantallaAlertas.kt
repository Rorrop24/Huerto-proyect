package cl.duoc.fotomotion.ui.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.AlertaHuerto

@Composable
fun PantallaAlertas(controlAlertas: AlertasViewModel) {
    val listaAlertas by controlAlertas.alertasVisibles.collectAsState()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Recordatorios", style = MaterialTheme.typography.titleLarge)
            Text("Son avisos informativos: no activan riego ni otros dispositivos.")
        }
        if (listaAlertas.isEmpty()) item { Text("No hay recordatorios por ahora.") }
        items(listaAlertas) { alerta -> TarjetaAlerta(alerta) }
    }
}

@Composable
private fun TarjetaAlerta(alerta: AlertaHuerto) {
    val colores = if (alerta.tipo.urgente) CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) else CardDefaults.cardColors()
    Card(Modifier.fillMaxWidth(), colors = colores) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(alerta.tipo.etiqueta, style = MaterialTheme.typography.labelLarge)
            Text(alerta.titulo, style = MaterialTheme.typography.titleMedium)
            Text(alerta.detalle)
        }
    }
}
