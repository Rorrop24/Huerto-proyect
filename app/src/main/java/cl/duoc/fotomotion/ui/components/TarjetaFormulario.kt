package cl.duoc.fotomotion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Tarjeta de formulario que se despliega al crear o editar y muestra una barra mientras se guarda.
@Composable
fun TarjetaFormulario(visible: Boolean, titulo: String, guardando: Boolean, contenido: @Composable ColumnScope.() -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        Card(Modifier.fillMaxWidth().animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                AnimatedVisibility(visible = guardando) { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                contenido()
            }
        }
    }
}
