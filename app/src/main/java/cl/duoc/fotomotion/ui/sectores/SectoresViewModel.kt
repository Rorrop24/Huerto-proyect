package cl.duoc.fotomotion.ui.sectores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.validation.ValidadorSectores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// Recibe el repositorio desde afuera: en la app lo entrega FabricaViewModels y en las pruebas uno con datos en memoria.
class SectoresViewModel(private val repositorio: RepositorioHuerto) : ViewModel() {
    private val _uiState = MutableStateFlow(SectoresUiState())
    val uiState: StateFlow<SectoresUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerSectores().collect { sectoresRecibidos ->
                _uiState.value = _uiState.value.copy(sectores = sectoresRecibidos)
            }
        }
    }

    // Un registro nuevo siempre empieza con el formulario vacío.
    fun abrirFormularioNuevo() {
        _uiState.value = SectoresUiState(sectores = _uiState.value.sectores, formularioVisible = true)
    }
    fun escribirNombre(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoNombre = textoNuevo, errorNombre = "", aviso = "")
    }
    fun escribirUbicacion(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoUbicacion = textoNuevo, errorUbicacion = "", aviso = "")
    }
    fun elegirEstado(estadoNuevo: EstadoSector) {
        _uiState.value = _uiState.value.copy(estadoElegido = estadoNuevo, aviso = "")
    }

    fun guardarSector(cuentaResponsable: Cuenta) {
        val formulario = _uiState.value
        if (!cuentaResponsable.esDocente || formulario.guardando) return
        val errores = ValidadorSectores.validar(formulario.textoNombre, formulario.textoUbicacion)
        _uiState.value = formulario.copy(errorNombre = errores.nombre, errorUbicacion = errores.ubicacion)
        if (!errores.esValido) return
        val sectorAnterior = formulario.sectorEnEdicion
        val registroNuevo = (sectorAnterior ?: Sector(responsableId = cuentaResponsable.codigo,
            fechaCreacion = LocalDate.now().toString())).copy(nombre = formulario.textoNombre.trim(),
            ubicacion = formulario.textoUbicacion.trim(), estado = formulario.estadoElegido,
            fechaActualizacion = LocalDate.now().toString())
        _uiState.value = _uiState.value.copy(guardando = true)
        viewModelScope.launch {
            try {
                if (sectorAnterior == null) repositorio.registrarSector(registroNuevo)
                else repositorio.actualizarSector(registroNuevo)
                limpiarFormulario()
                informar("Sector guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                // Se conservan los datos escritos para que el usuario pueda corregirlos.
                informar("No se pudo guardar el sector.")
            } finally {
                _uiState.value = _uiState.value.copy(guardando = false)
            }
        }
    }

    // Editar carga los datos del sector elegido en el formulario.
    fun editarSector(registroElegido: Sector) {
        _uiState.value = SectoresUiState(sectores = _uiState.value.sectores, formularioVisible = true,
            sectorEnEdicion = registroElegido, textoNombre = registroElegido.nombre,
            textoUbicacion = registroElegido.ubicacion, estadoElegido = registroElegido.estado,
            aviso = "Editando sector.")
    }
    // Cancelar o terminar de guardar deja el formulario vacío y cerrado.
    fun limpiarFormulario() {
        _uiState.value = SectoresUiState(sectores = _uiState.value.sectores)
    }
    fun eliminarSector(registroElegido: Sector) {
        viewModelScope.launch {
            try {
                val seBorro = repositorio.eliminarSector(registroElegido)
                if (seBorro && _uiState.value.sectorEnEdicion?.id == registroElegido.id) limpiarFormulario()
                informar(if (seBorro) "Sector eliminado." else "Primero elimina los cultivos y tareas de este sector.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo eliminar el sector.")
            }
        }
    }

    private fun informar(mensajeNuevo: String) {
        _uiState.value = _uiState.value.copy(aviso = mensajeNuevo)
    }
}
