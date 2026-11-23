package cl.duoc.fotomotion.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.alertas.GeneradorAlertas
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.visiblesPara
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

// Recibe el repositorio desde afuera: en la app lo entrega FabricaViewModels y en las pruebas uno con datos en memoria.
class AlertasViewModel(private val repositorio: RepositorioHuerto) : ViewModel() {
    private val cuentaActiva = MutableStateFlow<Cuenta?>(null)

    // Se recalcula cuando cambian las tareas, cultivos, problemas o la cuenta con sesión iniciada.
    // Un estudiante solo recibe recordatorios de sus propias tareas.
    val uiState: StateFlow<AlertasUiState> = combine(
        repositorio.leerTareas(), repositorio.leerCultivos(), repositorio.leerProblemas(), cuentaActiva
    ) { tareas, cultivos, problemas, cuenta ->
        if (cuenta == null) AlertasUiState()
        else AlertasUiState(GeneradorAlertas.generar(LocalDate.now(), tareas.visiblesPara(cuenta), cultivos, problemas))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlertasUiState())

    fun mostrarPara(cuenta: Cuenta?) { cuentaActiva.value = cuenta }
}
