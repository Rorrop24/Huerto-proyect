package cl.duoc.fotomotion.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.ui.theme.HuertoTema

// Etiqueta de estado (Pendiente, Aprobada...). El tono sale de TonosEstado.kt.
// El color y el texto cambian con una transición suave.
@Composable
fun EtiquetaEstado(texto: String, tono: TonoEstado) {
    val colores = HuertoTema.coloresEstado
    val esquema = MaterialTheme.colorScheme
    val fondo by animateColorAsState(when (tono) {
        TonoEstado.NEUTRO -> esquema.surfaceVariant
        TonoEstado.EXITO -> colores.exitoContenedor
        TonoEstado.ADVERTENCIA -> colores.advertenciaContenedor
        TonoEstado.ERROR -> esquema.errorContainer
    }, label = "fondoEstado")
    val letra by animateColorAsState(when (tono) {
        TonoEstado.NEUTRO -> esquema.onSurfaceVariant
        TonoEstado.EXITO -> colores.exito
        TonoEstado.ADVERTENCIA -> colores.advertencia
        TonoEstado.ERROR -> esquema.onErrorContainer
    }, label = "letraEstado")
    Surface(shape = RoundedCornerShape(50), color = fondo) {
        AnimatedContent(targetState = texto, label = "textoEstado") { textoVisible ->
            Text(textoVisible, color = letra, style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
    }
}
