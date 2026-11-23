package cl.duoc.fotomotion.ui.lecturas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaLecturas(viewModel: LecturasViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Simulación académica", style = MaterialTheme.typography.titleLarge)
            Text("Son valores de ejemplo. No vienen de sensores físicos ni activan riego.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(onClick = viewModel::simularRiego) { Text("Simular cambio de humedad") }
        }
        items(uiState.lecturas) { lectura -> TarjetaLectura(lectura) }
    }
}
