package cl.duoc.fotomotion.ui.lecturas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaLecturas(controlLecturas: LecturasViewModel) {
    val listaMediciones by controlLecturas.medicionesVisibles.collectAsState()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Simulación académica", style = MaterialTheme.typography.titleLarge)
            Text("Son valores de ejemplo. No vienen de sensores físicos ni cuentan como recurso nativo.")
            Button(onClick = controlLecturas::simularRiego) { Text("Simular cambio de humedad") }
        }
        items(listaMediciones) { medicion ->
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(medicion.lugarMedido, style = MaterialTheme.typography.titleMedium)
                    Text("Humedad del suelo: ${medicion.porcentajeAgua}%")
                    Text("Temperatura ambiente: ${medicion.gradosAmbiente} °C")
                    AnimatedVisibility(visible = medicion.porcentajeAgua < 30) {
                        Text("Humedad baja: revisar el riego.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
