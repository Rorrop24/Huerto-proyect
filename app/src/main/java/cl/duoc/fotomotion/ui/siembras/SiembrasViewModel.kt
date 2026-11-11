package cl.duoc.fotomotion.ui.siembras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class SiembrasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto.desdeContexto(aplicacion)
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
        val problemaNombre = ValidacionesHuerto.revisarTexto(formulario.textoSiembra, 3, 40, "Escribe el nombre del cultivo.")
        val problemaSeleccion = ValidacionesHuerto.revisarSector(formulario.parcelaElegida, formulario.opcionesParcela, "Selecciona un sector.")
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
                informarSiembra("Cultivo guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarSiembra("No se pudo guardar el cultivo.")
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
                informarSiembra(if (seBorro) "Cultivo eliminado." else "Primero elimina las tareas de este cultivo.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarSiembra("No se pudo eliminar el cultivo.")
            }
        }
    }

    private fun informarSiembra(mensajeNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(avisoSiembras = mensajeNuevo)
    }
}
