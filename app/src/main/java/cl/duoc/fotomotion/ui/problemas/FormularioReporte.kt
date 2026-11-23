package cl.duoc.fotomotion.ui.problemas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.ui.components.*

// Formulario para reportar un problema detectado en el huerto.
@Composable
internal fun FormularioReporte(uiState: ProblemasUiState, viewModel: ProblemasViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    TarjetaFormulario(visible = uiState.formularioVisible, titulo = "Reportar un problema",
        guardando = uiState.guardando) {
        ElegirSector(uiState.sectores, uiState.sectorElegido, uiState.errorSector,
            viewModel::elegirSector, habilitado)
        if (uiState.sectorElegido != null) ElegirCultivo(
            uiState.cultivos.filter { cultivo -> cultivo.sectorId == uiState.sectorElegido },
            uiState.cultivoElegido, uiState.errorCultivo, viewModel::elegirCultivo, habilitado)
        ElegirOpcion("Tipo de problema", TipoProblema.entries, uiState.tipoElegido, { tipo -> tipo.etiqueta },
            viewModel::elegirTipo, habilitado)
        ErrorHuerto(uiState.errorTipo, "Error de tipo de problema")
        CampoHuerto(uiState.textoDescripcion, "Descripción del problema", uiState.errorDescripcion,
            viewModel::escribirDescripcion, habilitado)
        Text("Foto opcional: solo plantas, cultivos o herramientas, sin personas.", style = MaterialTheme.typography.bodySmall)
        AnimatedVisibility(visible = uiState.direccionFoto.isNotBlank()) {
            FotoHuerto(uiState.direccionFoto)
        }
        AdjuntarFoto(viewModel::adjuntarFoto, viewModel::informar, habilitado)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.reportarProblema(cuentaActual) }, enabled = habilitado) { Text("Enviar reporte") }
            TextButton(onClick = viewModel::limpiarFormulario, enabled = habilitado) { Text("Cancelar") }
        }
    }
}
