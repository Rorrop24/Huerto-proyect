package cl.duoc.fotomotion.ui.siembras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.SiembraHuerto
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    fun elegirEstado(estadoNuevo: EstadoCultivo) {
        estadoMutable.value = estadoMutable.value.copy(estadoCultivo = estadoNuevo, avisoSiembras = "")
    }
    fun escribirFechaSiembra(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoFechaSiembra = textoNuevo, errorFechaSiembra = "")
    }
    fun escribirFechaTraslado(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoFechaTraslado = textoNuevo, errorFechaTraslado = "")
    }
    fun escribirFechaCosecha(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoFechaCosecha = textoNuevo, errorFechaCosecha = "")
    }
    fun escribirRiego(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoRiego = textoNuevo, errorRiego = "")
    }

    fun revisarSiembra(): Boolean {
        val formulario = estadoMutable.value
        val problemaNombre = ValidacionesHuerto.revisarTexto(formulario.textoSiembra, 3, 40, "Escribe el nombre del cultivo.")
        val problemaSeleccion = ValidacionesHuerto.revisarSector(formulario.parcelaElegida, formulario.opcionesParcela, "Selecciona un sector.")
        val problemaSiembra = ValidacionesHuerto.revisarFecha(formulario.textoFechaSiembra)
        val problemaTraslado = ValidacionesHuerto.revisarFechaOpcional(formulario.textoFechaTraslado).ifEmpty {
            ValidacionesHuerto.revisarOrden(formulario.textoFechaSiembra, formulario.textoFechaTraslado,
                "El trasplante no puede ser antes de la siembra.")
        }
        val problemaCosecha = ValidacionesHuerto.revisarFechaOpcional(formulario.textoFechaCosecha).ifEmpty {
            ValidacionesHuerto.revisarOrden(formulario.textoFechaSiembra, formulario.textoFechaCosecha,
                "La cosecha no puede ser antes de la siembra.")
        }
        val problemaRiego = ValidacionesHuerto.revisarNumero(formulario.textoRiego, 1, 30, "Indica cada cuántos días se riega.")
        estadoMutable.value = formulario.copy(errorSiembra = problemaNombre, errorSeleccion = problemaSeleccion,
            errorFechaSiembra = problemaSiembra, errorFechaTraslado = problemaTraslado,
            errorFechaCosecha = problemaCosecha, errorRiego = problemaRiego)
        return listOf(problemaNombre, problemaSeleccion, problemaSiembra, problemaTraslado, problemaCosecha, problemaRiego)
            .all { problema -> problema.isEmpty() }
    }

    fun confirmarSiembra() {
        if (estadoMutable.value.guardandoSiembra || !revisarSiembra()) return
        val formulario = estadoMutable.value
        val codigoParcela = formulario.parcelaElegida ?: return
        val siembraAnterior = formulario.siembraEnEdicion
        val registroNuevo = (siembraAnterior ?: SiembraHuerto(parcelaAsociada = codigoParcela, intervaloRiego = 1)).copy(
            tituloSiembra = formulario.textoSiembra.trim(), parcelaAsociada = codigoParcela,
            situacionSiembra = formulario.estadoCultivo.name, diaSiembra = formulario.textoFechaSiembra,
            diaTraslado = formulario.textoFechaTraslado.trim().ifBlank { null },
            diaCosecha = formulario.textoFechaCosecha.trim().ifBlank { null },
            intervaloRiego = formulario.textoRiego.trim().toInt())
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
            estadoCultivo = EstadoCultivo.desde(registroElegido.situacionSiembra),
            textoFechaSiembra = registroElegido.diaSiembra ?: "", textoFechaTraslado = registroElegido.diaTraslado ?: "",
            textoFechaCosecha = registroElegido.diaCosecha ?: "", textoRiego = registroElegido.intervaloRiego.toString(),
            errorSiembra = "", errorSeleccion = "", errorFechaSiembra = "", errorFechaTraslado = "",
            errorFechaCosecha = "", errorRiego = "", avisoSiembras = "Editando cultivo.")
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
