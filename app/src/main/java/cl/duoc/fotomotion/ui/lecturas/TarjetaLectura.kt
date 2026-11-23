package cl.duoc.fotomotion.ui.lecturas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.LecturaSensor

@Composable
internal fun TarjetaLectura(lectura: LecturaSensor) {
    // La barra de humedad se llena o vacía de forma gradual cuando cambia el valor.
    val humedadAnimada by animateFloatAsState(lectura.humedadSuelo / 100f, animationSpec = tween(700), label = "humedadSuelo")
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(lectura.lugar, style = MaterialTheme.typography.titleMedium)
            AnimatedContent(targetState = lectura.humedadSuelo, label = "valorHumedad") { porcentaje ->
                Text("Humedad del suelo: $porcentaje%")
            }
            LinearProgressIndicator(progress = { humedadAnimada }, modifier = Modifier.fillMaxWidth(),
                color = if (lectura.humedadBaja) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            Text("Temperatura ambiente: ${lectura.temperatura} °C")
            Text("Humedad ambiental: ${lectura.humedadAmbiente}%")
            AnimatedVisibility(visible = lectura.humedadBaja) {
                Text("Humedad baja: revisar el riego.", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
