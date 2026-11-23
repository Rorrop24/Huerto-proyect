package cl.duoc.fotomotion.ui.problemas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun PantallaProblemas(viewModel: ProblemasViewModel, cuentaActual: Cuenta) {
    val uiState by viewModel.uiState.collectAsState()
    val permiteSeguimiento = cuentaActual.esDocente
    val posicionLista = rememberLazyListState()
    val accionesPantalla = rememberCoroutineScope()

    LazyColumn(state = posicionLista, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Problemas reportados", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                if (!uiState.formularioVisible) {
                    FilledTonalButton(onClick = viewModel::abrirReporte) { Text("Reportar problema") }
                }
            }
        }
        item { AvisoHuerto(uiState.aviso) }
        val problemaEnSeguimiento = uiState.problemaEnSeguimiento
        if (permiteSeguimiento && problemaEnSeguimiento != null) item {
            FormularioSeguimiento(uiState, problemaEnSeguimiento, viewModel, cuentaActual)
        }
        item { FormularioReporte(uiState, viewModel, cuentaActual) }
        if (uiState.problemas.isEmpty()) item { Text("No hay problemas reportados.") }
        items(uiState.problemas, key = { problema -> problema.id }) { problema ->
            val nombreSector = uiState.sectores.find { sector -> sector.id == problema.sectorId }?.nombre ?: "Sector no disponible"
            val nombreCultivo = uiState.cultivos.find { cultivo -> cultivo.id == problema.cultivoId }?.nombre
            TarjetaProblema(problema, nombreSector, nombreCultivo, permiteSeguimiento, habilitado = !uiState.guardando,
                alSeguir = {
                    viewModel.iniciarSeguimiento(problema)
                    accionesPantalla.launch { posicionLista.animateScrollToItem(0) }
                })
        }
    }
}
