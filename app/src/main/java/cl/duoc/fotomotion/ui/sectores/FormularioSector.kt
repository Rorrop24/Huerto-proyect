package cl.duoc.fotomotion.ui.sectores

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.ui.components.*

@Composable
internal fun FormularioSector(uiState: SectoresUiState, viewModel: SectoresViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    TarjetaFormulario(visible = uiState.formularioVisible,
        titulo = if (uiState.sectorEnEdicion == null) "Nuevo sector" else "Editar sector",
        guardando = uiState.guardando) {
        CampoHuerto(uiState.textoNombre, "Nombre del sector", uiState.errorNombre,
            viewModel::escribirNombre, habilitado)
        CampoHuerto(uiState.textoUbicacion, "Ubicación", uiState.errorUbicacion,
            viewModel::escribirUbicacion, habilitado)
        ElegirOpcion("Estado del sector", EstadoSector.entries, uiState.estadoElegido,
            { estado -> estado.etiqueta }, viewModel::elegirEstado, habilitado)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.guardarSector(cuentaActual) }, enabled = habilitado) { Text("Guardar sector") }
            TextButton(onClick = viewModel::limpiarFormulario, enabled = habilitado) { Text("Cancelar") }
        }
    }
}
