package cl.duoc.fotomotion.ui.alertas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.alertas.GeneradorAlertas
import cl.duoc.fotomotion.domain.model.AlertaHuerto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class AlertasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto.desdeContexto(aplicacion)

    // Los recordatorios se recalculan cada vez que cambian las tareas, cultivos o problemas.
    val alertasVisibles: StateFlow<List<AlertaHuerto>> = combine(
        repositorio.leerLabores(), repositorio.leerSiembras(), repositorio.leerProblemas()
    ) { labores, siembras, problemas ->
        GeneradorAlertas.generar(LocalDate.now(), labores, siembras, problemas)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
