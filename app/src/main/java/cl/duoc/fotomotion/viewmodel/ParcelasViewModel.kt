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

data class EstadoParcelas(
    val listaParcelas: List<ParcelaHuerto> = emptyList(),
    val textoParcela: String = "",
    val textoLugar: String = "",
    val errorParcela: String = "",
    val errorLugar: String = "",
    val parcelaEnEdicion: ParcelaHuerto? = null,
    val avisoParcelas: String = "",
    val guardandoParcela: Boolean = false
)

class ParcelasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto(BaseHuerto.abrirBase(aplicacion).consultasHuerto())
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
        var problemaNombre = ""
        var problemaLugar = ""
        if (formulario.textoParcela.isBlank()) {
            problemaNombre = "Escribe el nombre del sector."
        } else if (formulario.textoParcela.trim().length < 3 || formulario.textoParcela.length > 40) {
            problemaNombre = "Usa entre 3 y 40 caracteres."
        }
        if (formulario.textoLugar.isBlank()) {
            problemaLugar = "Escribe dónde está el sector."
        } else if (formulario.textoLugar.trim().length < 3 || formulario.textoLugar.length > 80) {
            problemaLugar = "Usa entre 3 y 80 caracteres."
        }
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
                estadoMutable.value = estadoMutable.value.copy(avisoParcelas = "Sector guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                estadoMutable.value = estadoMutable.value.copy(avisoParcelas = "No se pudo guardar el sector.")
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
                estadoMutable.value = estadoMutable.value.copy(avisoParcelas = if (seBorro)
                    "Sector eliminado." else "Primero elimina los cultivos y tareas de este sector.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                estadoMutable.value = estadoMutable.value.copy(avisoParcelas = "No se pudo eliminar el sector.")
            }
        }
    }
}
