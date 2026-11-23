package cl.duoc.fotomotion.ui.sectores

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
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun PantallaSectores(viewModel: SectoresViewModel, cuentaActual: Cuenta) {
    val uiState by viewModel.uiState.collectAsState()
    val permiteCambios = cuentaActual.esDocente
    // Se guarda solo el id para que el diálogo siga abierto si se gira la pantalla.
    var idPorBorrar by rememberSaveable { mutableStateOf<Int?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("${uiState.sectores.size} sectores registrados", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                if (permiteCambios && !uiState.formularioVisible) {
                    FilledTonalButton(onClick = viewModel::abrirFormularioNuevo) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Nuevo sector")
                    }
                }
            }
        }
        item { AvisoHuerto(uiState.aviso) }
        if (permiteCambios) item { FormularioSector(uiState, viewModel, cuentaActual) }
        if (uiState.sectores.isEmpty()) item { Text("Todavía no hay sectores. El docente puede crear el primero.") }
        items(uiState.sectores, key = { sector -> sector.id }) { sector ->
            TarjetaSector(sector, permiteCambios, habilitado = !uiState.guardando,
                alEditar = {
                    viewModel.editarSector(sector)
                    accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                },
                alBorrar = { idPorBorrar = sector.id })
        }
    }
    uiState.sectores.find { sector -> sector.id == idPorBorrar }?.let { sector ->
        ConfirmarBorrado(sector.nombre, alBorrar = {
            viewModel.eliminarSector(sector)
            idPorBorrar = null
        }, alCancelar = { idPorBorrar = null })
    }
}
