package cl.duoc.fotomotion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresHuerto = lightColorScheme(
    primary = Color(0xFF356B45),
    secondary = Color(0xFF765B36),
    background = Color(0xFFFAFCF7)
)

@Composable
fun TemaHuerto(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColoresHuerto, content = contenido)
}
