package cl.duoc.fotomotion.ui.cultivos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.validation.ValidadorCultivos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Recibe el repositorio desde afuera: en la app lo entrega FabricaViewModels y en las pruebas uno con datos en memoria.
class CultivosViewModel(private val repositorio: RepositorioHuerto) : ViewModel() {
    private val _uiState = MutableStateFlow(CultivosUiState())
    val uiState: StateFlow<CultivosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerCultivos().collect { cultivosRecibidos ->
                _uiState.value = _uiState.value.copy(cultivos = cultivosRecibidos)
            }
        }
        viewModelScope.launch {
            repositorio.leerSectores().collect { sectoresRecibidos ->
                _uiState.value = _uiState.value.copy(sectores = sectoresRecibidos)
            }
        }
    }

    fun abrirFormularioNuevo() {
        _uiState.value = formularioVacio().copy(formularioVisible = true)
    }
    fun escribirNombre(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoNombre = textoNuevo, errorNombre = "", aviso = "")
    }
    fun elegirSector(codigoElegido: Int) {
        _uiState.value = _uiState.value.copy(sectorElegido = codigoElegido, errorSector = "")
    }
    fun elegirEstado(estadoNuevo: EstadoCultivo) {
        _uiState.value = _uiState.value.copy(estadoCultivo = estadoNuevo, aviso = "")
    }
    fun escribirFechaSiembra(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoFechaSiembra = textoNuevo, errorFechaSiembra = "")
    }
    fun escribirFechaTrasplante(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoFechaTrasplante = textoNuevo, errorFechaTrasplante = "")
    }
    fun escribirFechaCosecha(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoFechaCosecha = textoNuevo, errorFechaCosecha = "")
    }
    fun escribirRiego(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoRiego = textoNuevo, errorRiego = "")
    }

    fun guardarCultivo(cuentaResponsable: Cuenta) {
        val formulario = _uiState.value
        if (!cuentaResponsable.esDocente || formulario.guardando) return
        val errores = ValidadorCultivos.validar(formulario.textoNombre, formulario.sectorElegido, formulario.sectores,
            formulario.textoFechaSiembra, formulario.textoFechaTrasplante, formulario.textoFechaCosecha, formulario.textoRiego)
        _uiState.value = formulario.copy(errorNombre = errores.nombre, errorSector = errores.sector,
            errorFechaSiembra = errores.fechaSiembra, errorFechaTrasplante = errores.fechaTrasplante,
            errorFechaCosecha = errores.fechaCosecha, errorRiego = errores.riego)
        if (!errores.esValido) return
        val id = formulario.sectorElegido ?: return
        val cultivoAnterior = formulario.cultivoEnEdicion
        val registroNuevo = (cultivoAnterior ?: Cultivo(sectorId = id, frecuenciaRiegoDias = 1)).copy(
            nombre = formulario.textoNombre.trim(), sectorId = id,
            estado = formulario.estadoCultivo, fechaSiembra = formulario.textoFechaSiembra,
            fechaTrasplante = formulario.textoFechaTrasplante.trim().ifBlank { null },
            fechaCosecha = formulario.textoFechaCosecha.trim().ifBlank { null },
            frecuenciaRiegoDias = formulario.textoRiego.trim().toInt())
        _uiState.value = _uiState.value.copy(guardando = true)
        viewModelScope.launch {
            try {
                if (cultivoAnterior == null) repositorio.registrarCultivo(registroNuevo)
                else repositorio.actualizarCultivo(registroNuevo)
                limpiarFormulario()
                informar("Cultivo guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo guardar el cultivo.")
            } finally {
                _uiState.value = _uiState.value.copy(guardando = false)
            }
        }
    }

    fun editarCultivo(registroElegido: Cultivo) {
        _uiState.value = formularioVacio().copy(formularioVisible = true, cultivoEnEdicion = registroElegido,
            textoNombre = registroElegido.nombre, sectorElegido = registroElegido.sectorId,
            estadoCultivo = registroElegido.estado,
            textoFechaSiembra = registroElegido.fechaSiembra ?: "", textoFechaTrasplante = registroElegido.fechaTrasplante ?: "",
            textoFechaCosecha = registroElegido.fechaCosecha ?: "", textoRiego = registroElegido.frecuenciaRiegoDias.toString(),
            aviso = "Editando cultivo.")
    }
    fun limpiarFormulario() {
        _uiState.value = formularioVacio()
    }
    fun eliminarCultivo(registroElegido: Cultivo) {
        viewModelScope.launch {
            try {
                val seBorro = repositorio.eliminarCultivo(registroElegido)
                if (seBorro && _uiState.value.cultivoEnEdicion?.id == registroElegido.id) limpiarFormulario()
                informar(if (seBorro) "Cultivo eliminado." else "Primero elimina las tareas de este cultivo.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo eliminar el cultivo.")
            }
        }
    }

    // Estado inicial conservando las listas que vienen de la base.
    private fun formularioVacio() = CultivosUiState(cultivos = _uiState.value.cultivos,
        sectores = _uiState.value.sectores)

    private fun informar(mensajeNuevo: String) {
        _uiState.value = _uiState.value.copy(aviso = mensajeNuevo)
    }
}
