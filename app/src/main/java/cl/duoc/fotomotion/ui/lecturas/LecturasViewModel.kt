package cl.duoc.fotomotion.ui.lecturas

import androidx.lifecycle.ViewModel
import cl.duoc.fotomotion.domain.model.LecturaSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Lecturas de ejemplo: no se conectan a sensores físicos.
class LecturasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LecturasUiState(lecturas = listOf(
        LecturaSensor("Invernadero de ejemplo", humedadSuelo = 22, temperatura = 25, humedadAmbiente = 70),
        LecturaSensor("Patio de ejemplo", humedadSuelo = 65, temperatura = 22, humedadAmbiente = 55)
    )))
    val uiState: StateFlow<LecturasUiState> = _uiState.asStateFlow()

    // Alterna la humedad del invernadero entre seca (22 %) y regada (60 %).
    fun simularRiego() {
        val actual = _uiState.value
        val primeraLectura = actual.lecturas.first()
        val regada = primeraLectura.copy(humedadSuelo = if (primeraLectura.humedadBaja) 60 else 22)
        _uiState.value = actual.copy(lecturas = listOf(regada) + actual.lecturas.drop(1),
            cantidadSimulaciones = actual.cantidadSimulaciones + 1)
    }
}
