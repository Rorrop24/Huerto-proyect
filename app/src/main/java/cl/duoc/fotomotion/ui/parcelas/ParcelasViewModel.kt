package cl.duoc.fotomotion.ui.parcelas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.ParcelaHuerto
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ParcelasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto.desdeContexto(aplicacion)
    private val estadoMutable = MutableStateFlow(EstadoParcelas())
    val estadoParcelas = estadoMutable.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerParcelas().collect { parcelasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(listaParcelas = parcelasRecibidas)
            }
        }
    }

    fun escribirParcela(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoParcela = textoNuevo, errorParcela = "", avisoParcelas = "")
    }
    fun escribirLugar(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoLugar = textoNuevo, errorLugar = "", avisoParcelas = "")
    }

    fun revisarParcela(): Boolean {
        val formulario = estadoMutable.value
        val problemaNombre = ValidacionesHuerto.revisarTexto(formulario.textoParcela, 3, 40, "Escribe el nombre del sector.")
        val problemaLugar = ValidacionesHuerto.revisarTexto(formulario.textoLugar, 3, 80, "Escribe dónde está el sector.")
        estadoMutable.value = formulario.copy(errorParcela = problemaNombre, errorLugar = problemaLugar)
        return problemaNombre.isEmpty() && problemaLugar.isEmpty()
    }

    fun confirmarParcela() {
        if (estadoMutable.value.guardandoParcela || !revisarParcela()) return
        val formulario = estadoMutable.value
        val parcelaAnterior = formulario.parcelaEnEdicion
        val registroNuevo = (parcelaAnterior ?: ParcelaHuerto(
            tituloParcela = "", lugarParcela = "", situacionParcela = "EN_USO",
            encargadoParcela = "USR-001", diaRegistro = LocalDate.now().toString()
        )).copy(tituloParcela = formulario.textoParcela.trim(), lugarParcela = formulario.textoLugar.trim(),
            diaCambio = LocalDate.now().toString())
        estadoMutable.value = formulario.copy(guardandoParcela = true)
        viewModelScope.launch {
            try {
                if (parcelaAnterior == null) repositorio.registrarParcela(registroNuevo)
                else repositorio.corregirParcela(registroNuevo)
                limpiarParcela()
                informarParcela("Sector guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarParcela("No se pudo guardar el sector.")
            } finally {
                estadoMutable.value = estadoMutable.value.copy(guardandoParcela = false)
            }
        }
    }

    fun prepararParcela(registroElegido: ParcelaHuerto) {
        estadoMutable.value = estadoMutable.value.copy(parcelaEnEdicion = registroElegido,
            textoParcela = registroElegido.tituloParcela, textoLugar = registroElegido.lugarParcela,
            errorParcela = "", errorLugar = "", avisoParcelas = "Editando sector.")
    }
    fun limpiarParcela() {
        estadoMutable.value = EstadoParcelas(listaParcelas = estadoMutable.value.listaParcelas)
    }
    fun eliminarParcela(registroElegido: ParcelaHuerto) {
        viewModelScope.launch {
            try {
                val seBorro = repositorio.borrarParcela(registroElegido)
                if (seBorro && estadoMutable.value.parcelaEnEdicion?.codigoParcela == registroElegido.codigoParcela) limpiarParcela()
                informarParcela(if (seBorro) "Sector eliminado." else "Primero elimina los cultivos y tareas de este sector.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarParcela("No se pudo eliminar el sector.")
            }
        }
    }

    private fun informarParcela(mensajeNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(avisoParcelas = mensajeNuevo)
    }
}
