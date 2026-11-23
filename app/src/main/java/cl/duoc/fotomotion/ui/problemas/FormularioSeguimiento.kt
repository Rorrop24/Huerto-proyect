package cl.duoc.fotomotion.ui.problemas

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.ui.components.*

// Formulario del docente para cambiar el estado de un reporte y dejar un comentario.
@Composable
internal fun FormularioSeguimiento(uiState: ProblemasUiState, problema: Problema,
    viewModel: ProblemasViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    Card(Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Seguimiento: ${problema.tipo.etiqueta}", style = MaterialTheme.typography.titleMedium)
            Text(problema.descripcion)
            ElegirOpcion("Estado del reporte", EstadoProblema.entries, uiState.estadoSeguimiento,
                { estado -> estado.etiqueta }, viewModel::elegirEstadoSeguimiento, habilitado)
            CampoHuerto(uiState.textoSeguimiento, "Comentario de seguimiento (opcional)", uiState.errorSeguimiento,
                viewModel::escribirSeguimiento, habilitado)
            Button(onClick = { viewModel.guardarSeguimiento(cuentaActual) }, enabled = habilitado) { Text("Guardar seguimiento") }
            TextButton(onClick = viewModel::cancelarSeguimiento, enabled = habilitado) { Text("Cancelar seguimiento") }
        }
    }
}
