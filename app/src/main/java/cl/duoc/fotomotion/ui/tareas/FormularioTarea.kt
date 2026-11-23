package cl.duoc.fotomotion.ui.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.TipoTarea
import cl.duoc.fotomotion.ui.components.*

// Formulario del docente para crear o editar una tarea y asignarla a un estudiante.
@Composable
internal fun FormularioTarea(uiState: TareasUiState, viewModel: TareasViewModel, cuentaActual: Cuenta) {
    val habilitado = !uiState.guardando
    TarjetaFormulario(visible = uiState.formularioVisible,
        titulo = if (uiState.tareaEnEdicion == null) "Nueva tarea" else "Editar tarea",
        guardando = uiState.guardando) {
        CampoHuerto(uiState.textoDescripcion, "Descripción de la tarea", uiState.errorDescripcion,
            viewModel::escribirDescripcion, habilitado)
        CampoHuerto(uiState.textoFecha, "Fecha (AAAA-MM-DD)", uiState.errorFecha,
            viewModel::escribirFecha, habilitado)
        ElegirOpcion("Tipo de tarea", TipoTarea.entries, uiState.tipoElegido, { tipo -> tipo.etiqueta },
            viewModel::elegirTipo, habilitado)
        ElegirSector(uiState.sectores, uiState.sectorElegido, uiState.errorSector,
            viewModel::elegirSector, habilitado)
        if (uiState.sectorElegido != null) ElegirCultivo(
            uiState.cultivos.filter { cultivo -> cultivo.sectorId == uiState.sectorElegido },
            uiState.cultivoElegido, uiState.errorCultivo, viewModel::elegirCultivo, habilitado)
        ElegirOpcion("Asignar a", uiState.estudiantes,
            uiState.estudiantes.find { cuenta -> cuenta.codigo == uiState.estudianteAsignado },
            { cuenta -> cuenta.nombre }, viewModel::elegirEstudiante, habilitado)
        ErrorHuerto(uiState.errorEstudiante, "Error de asignación")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.guardarTarea(cuentaActual) }, enabled = habilitado) { Text("Guardar tarea") }
            TextButton(onClick = viewModel::limpiarFormulario, enabled = habilitado) { Text("Cancelar") }
        }
    }
}
