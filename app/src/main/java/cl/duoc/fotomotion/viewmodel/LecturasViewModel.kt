package cl.duoc.fotomotion.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.fotomotion.model.MedicionHuerto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LecturasViewModel : ViewModel() {
    private val medicionesMutables = MutableStateFlow(listOf(
        MedicionHuerto("Invernadero de ejemplo", 22, 25),
        MedicionHuerto("Patio de ejemplo", 65, 22)
    ))
    val medicionesVisibles = medicionesMutables.asStateFlow()

    fun simularRiego() {
        val primeraLectura = medicionesMutables.value.first()
        medicionesMutables.value = listOf(
            primeraLectura.copy(porcentajeAgua = if (primeraLectura.porcentajeAgua < 30) 60 else 22),
            MedicionHuerto("Patio de ejemplo", 65, 22)
        )
    }
}
