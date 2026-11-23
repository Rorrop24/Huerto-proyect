package cl.duoc.fotomotion.ui.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.visiblesPara
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun PantallaTareas(viewModel: TareasViewModel, cuentaActual: Cuenta) {
    val uiState by viewModel.uiState.collectAsState()
    val esDocente = cuentaActual.esDocente
    // El docente ve todas las tareas; el estudiante solo las que tiene asignadas.
    val tareasVisibles = uiState.tareas.visiblesPara(cuentaActual)
    var idPorBorrar by rememberSaveable { mutableStateOf<Int?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    val subirAlInicio: () -> Unit = { accionesPantalla.launch { posicionLista.animateScrollToItem(0) } }

    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (esDocente) "Tareas registradas" else "Mis tareas", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                if (esDocente && !uiState.formularioVisible) {
                    FilledTonalButton(onClick = viewModel::abrirFormularioNuevo) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Nueva tarea")
                    }
                }
            }
        }
        item { AvisoHuerto(uiState.aviso) }
        if (esDocente) item { FormularioTarea(uiState, viewModel, cuentaActual) }
        if (!esDocente) item { FormularioEntrega(uiState, viewModel, cuentaActual) }
        if (tareasVisibles.isEmpty()) item {
            Text(if (esDocente) "Todavía no hay tareas. Crea la primera con \"Nueva tarea\"." else "No tienes tareas asignadas.")
        }
        items(tareasVisibles, key = { tarea -> tarea.id }) { tarea ->
            val nombreSector = uiState.sectores.find { sector -> sector.id == tarea.sectorId }?.nombre ?: "Sector no disponible"
            val nombreCultivo = uiState.cultivos.find { cultivo -> cultivo.id == tarea.cultivoId }?.nombre
            val nombreEstudiante = uiState.estudiantes.find { cuenta -> cuenta.codigo == tarea.responsableId }?.nombre ?: "Sin asignar"
            TarjetaTarea(tarea, nombreSector, nombreCultivo, nombreEstudiante, esDocente, habilitado = !uiState.guardando,
                alRealizar = {
                    viewModel.iniciarEntrega(tarea)
                    subirAlInicio()
                },
                alRevisar = { aceptarTrabajo -> viewModel.revisarEntrega(tarea, aceptarTrabajo, cuentaActual) },
                alEditar = {
                    viewModel.editarTarea(tarea)
                    subirAlInicio()
                },
                alBorrar = { idPorBorrar = tarea.id })
        }
    }
    uiState.tareas.find { tarea -> tarea.id == idPorBorrar }?.let { tarea ->
        ConfirmarBorrado(tarea.descripcion, alBorrar = {
            viewModel.eliminarTarea(tarea)
            idPorBorrar = null
        }, alCancelar = { idPorBorrar = null })
    }
}
