package cl.duoc.fotomotion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.model.*
import cl.duoc.fotomotion.repository.RepositorioHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EstadoSiembras(
    val listaSiembras: List<SiembraHuerto> = emptyList(),
    val opcionesParcela: List<ParcelaHuerto> = emptyList(),
    val textoSiembra: String = "",
    val parcelaElegida: Int? = null,
    val errorSiembra: String = "",
    val errorSeleccion: String = "",
    val siembraEnEdicion: SiembraHuerto? = null,
    val avisoSiembras: String = "",
    val guardandoSiembra: Boolean = false
)

class SiembrasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto(BaseHuerto.abrirBase(aplicacion).consultasHuerto())
    private val estadoMutable = MutableStateFlow(EstadoSiembras())
    val estadoSiembras = estadoMutable.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerSiembras().collect { siembrasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(listaSiembras = siembrasRecibidas)
            }
        }
        viewModelScope.launch {
            repositorio.leerParcelas().collect { parcelasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(opcionesParcela = parcelasRecibidas)
            }
        }
    }
    fun escribirSiembra(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoSiembra = textoNuevo, errorSiembra = "", avisoSiembras = "")
    }
    fun elegirParcela(codigoElegido: Int) {
        estadoMutable.value = estadoMutable.value.copy(parcelaElegida = codigoElegido, errorSeleccion = "")
    }
    fun revisarSiembra(): Boolean {
        val formulario = estadoMutable.value
        var problemaNombre = ""
        var problemaSeleccion = ""
        if (formulario.textoSiembra.isBlank()) problemaNombre = "Escribe el nombre del cultivo."
        else if (formulario.textoSiembra.trim().length < 3 || formulario.textoSiembra.length > 40)
            problemaNombre = "Usa entre 3 y 40 caracteres."
        if (formulario.parcelaElegida == null) problemaSeleccion = "Selecciona un sector."
        else if (formulario.opcionesParcela.none { parcela -> parcela.codigoParcela == formulario.parcelaElegida })
            problemaSeleccion = "El sector ya no está disponible."
        estadoMutable.value = formulario.copy(errorSiembra = problemaNombre, errorSeleccion = problemaSeleccion)
        return problemaNombre.isEmpty() && problemaSeleccion.isEmpty()
    }
    fun confirmarSiembra() {
        if (estadoMutable.value.guardandoSiembra || !revisarSiembra()) return
        val formulario = estadoMutable.value
        val codigoParcela = formulario.parcelaElegida ?: return
        val siembraAnterior = formulario.siembraEnEdicion
        val registroNuevo = (siembraAnterior ?: SiembraHuerto(tituloSiembra = "",
            parcelaAsociada = codigoParcela, intervaloRiego = 1, situacionSiembra = "SEMBRADO",
            diaSiembra = LocalDate.now().toString())).copy(
                tituloSiembra = formulario.textoSiembra.trim(), parcelaAsociada = codigoParcela)
        estadoMutable.value = formulario.copy(guardandoSiembra = true)
        viewModelScope.launch {
            try {
                if (siembraAnterior == null) repositorio.registrarSiembra(registroNuevo)
                else repositorio.corregirSiembra(registroNuevo)
                limpiarSiembra()
                estadoMutable.value = estadoMutable.value.copy(avisoSiembras = "Cultivo guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                estadoMutable.value = estadoMutable.value.copy(avisoSiembras = "No se pudo guardar el cultivo.")
            } finally {
                estadoMutable.value = estadoMutable.value.copy(guardandoSiembra = false)
            }
        }
    }
    fun prepararSiembra(registroElegido: SiembraHuerto) {
        estadoMutable.value = estadoMutable.value.copy(siembraEnEdicion = registroElegido,
            textoSiembra = registroElegido.tituloSiembra, parcelaElegida = registroElegido.parcelaAsociada,
            errorSiembra = "", errorSeleccion = "", avisoSiembras = "Editando cultivo.")
    }
    fun limpiarSiembra() {
        estadoMutable.value = EstadoSiembras(listaSiembras = estadoMutable.value.listaSiembras,
            opcionesParcela = estadoMutable.value.opcionesParcela)
    }
    fun eliminarSiembra(registroElegido: SiembraHuerto) {
        viewModelScope.launch {
            try {
                val seBorro = repositorio.borrarSiembra(registroElegido)
                if (seBorro && estadoMutable.value.siembraEnEdicion?.codigoSiembra == registroElegido.codigoSiembra) limpiarSiembra()
                estadoMutable.value = estadoMutable.value.copy(avisoSiembras = if (seBorro)
                    "Cultivo eliminado." else "Primero elimina las tareas de este cultivo.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                estadoMutable.value = estadoMutable.value.copy(avisoSiembras = "No se pudo eliminar el cultivo.")
            }
        }
    }
}
