package cl.duoc.fotomotion.ui.cultivos

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
fun PantallaCultivos(viewModel: CultivosViewModel, cuentaActual: Cuenta) {
    val uiState by viewModel.uiState.collectAsState()
    val permiteCambios = cuentaActual.esDocente
    var idPorBorrar by rememberSaveable { mutableStateOf<Int?>(null) }
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()
    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Cultivos registrados", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                if (permiteCambios && !uiState.formularioVisible) {
                    FilledTonalButton(onClick = viewModel::abrirFormularioNuevo) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Nuevo cultivo")
                    }
                }
            }
        }
        item { AvisoHuerto(uiState.aviso) }
        if (permiteCambios) item { FormularioCultivo(uiState, viewModel, cuentaActual) }
        if (uiState.cultivos.isEmpty()) item { Text("Todavía no hay cultivos.") }
        items(uiState.cultivos, key = { cultivo -> cultivo.id }) { cultivo ->
            val nombreSector = uiState.sectores.find { sector -> sector.id == cultivo.sectorId }?.nombre ?: "Sector no disponible"
            TarjetaCultivo(cultivo, nombreSector, permiteCambios, habilitado = !uiState.guardando,
                alEditar = {
                    viewModel.editarCultivo(cultivo)
                    accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                },
                alBorrar = { idPorBorrar = cultivo.id })
        }
    }
    uiState.cultivos.find { cultivo -> cultivo.id == idPorBorrar }?.let { cultivo ->
        ConfirmarBorrado(cultivo.nombre, alBorrar = {
            viewModel.eliminarCultivo(cultivo)
            idPorBorrar = null
        }, alCancelar = { idPorBorrar = null })
    }
}
