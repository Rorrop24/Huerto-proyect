package cl.duoc.fotomotion.ui.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Alerta

@Composable
internal fun TarjetaAlerta(alerta: Alerta) {
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
