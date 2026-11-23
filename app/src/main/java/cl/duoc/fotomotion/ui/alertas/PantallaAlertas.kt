package cl.duoc.fotomotion.ui.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Composable
fun PantallaAlertas(viewModel: AlertasViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val listaAlertas = uiState.alertas
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Recordatorios", style = MaterialTheme.typography.titleLarge)
            Text("Son avisos informativos: no activan riego ni otros dispositivos.")
        }
        if (listaAlertas.isEmpty()) item { Text("No hay recordatorios por ahora.") }
        items(listaAlertas) { alerta -> TarjetaAlerta(alerta) }
    }
}
