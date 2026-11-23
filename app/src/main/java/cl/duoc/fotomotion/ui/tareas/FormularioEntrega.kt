package cl.duoc.fotomotion.ui.tareas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.ui.components.*

// Formulario del estudiante para entregar la evidencia de una tarea.
@Composable
internal fun FormularioEntrega(uiState: TareasUiState, viewModel: TareasViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    TarjetaFormulario(visible = uiState.tareaEnEntrega != null,
        titulo = "Entregar: ${uiState.tareaEnEntrega?.descripcion.orEmpty()}",
        guardando = uiState.guardando) {
        AnimatedVisibility(visible = uiState.direccionFoto.isNotBlank()) {
            FotoHuerto(uiState.direccionFoto)
        }
        AdjuntarFoto(viewModel::adjuntarFoto, viewModel::informar, habilitado)
        ErrorHuerto(uiState.errorFoto, "Error de fotografía")
        CampoHuerto(uiState.textoComentario, "Comentario del trabajo", uiState.errorComentario,
            viewModel::escribirComentario, habilitado)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.entregarTarea(cuentaActual) }, enabled = habilitado) { Text("Enviar evidencia") }
            TextButton(onClick = viewModel::limpiarFormulario, enabled = habilitado) { Text("Cancelar entrega") }
        }
    }
}
