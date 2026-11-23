package cl.duoc.fotomotion.ui.cultivos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.ui.components.*

@Composable
internal fun FormularioCultivo(uiState: CultivosUiState, viewModel: CultivosViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    TarjetaFormulario(visible = uiState.formularioVisible,
        titulo = if (uiState.cultivoEnEdicion == null) "Nuevo cultivo" else "Editar cultivo",
        guardando = uiState.guardando) {
        CampoHuerto(uiState.textoNombre, "Nombre del cultivo", uiState.errorNombre,
            viewModel::escribirNombre, habilitado)
        ElegirSector(uiState.sectores, uiState.sectorElegido, uiState.errorSector,
            viewModel::elegirSector, habilitado)
        ElegirOpcion("Estado del cultivo", EstadoCultivo.entries, uiState.estadoCultivo,
            { estado -> estado.etiqueta }, viewModel::elegirEstado, habilitado)
        CampoHuerto(uiState.textoFechaSiembra, "Fecha de siembra (AAAA-MM-DD)", uiState.errorFechaSiembra,
            viewModel::escribirFechaSiembra, habilitado)
        CampoHuerto(uiState.textoFechaTrasplante, "Fecha de trasplante (opcional)", uiState.errorFechaTrasplante,
            viewModel::escribirFechaTrasplante, habilitado)
        CampoHuerto(uiState.textoFechaCosecha, "Cosecha estimada (opcional)", uiState.errorFechaCosecha,
            viewModel::escribirFechaCosecha, habilitado)
        CampoHuerto(uiState.textoRiego, "Riego cada (días)", uiState.errorRiego,
            viewModel::escribirRiego, habilitado, tipoTeclado = KeyboardType.Number)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.guardarCultivo(cuentaActual) }, enabled = habilitado) { Text("Guardar cultivo") }
            TextButton(onClick = viewModel::limpiarFormulario, enabled = habilitado) { Text("Cancelar") }
        }
    }
}
