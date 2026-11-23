package cl.duoc.fotomotion.ui.problemas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.Problema
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.domain.validation.ValidadorProblemas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// Recibe el repositorio desde afuera: en la app lo entrega FabricaViewModels y en las pruebas uno con datos en memoria.
class ProblemasViewModel(private val repositorio: RepositorioHuerto) : ViewModel() {
    private val _uiState = MutableStateFlow(ProblemasUiState())
    val uiState: StateFlow<ProblemasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerProblemas().collect { problemasRecibidos ->
                _uiState.value = _uiState.value.copy(problemas = problemasRecibidos)
            }
        }
        viewModelScope.launch {
            repositorio.leerSectores().collect { sectoresRecibidos ->
                _uiState.value = _uiState.value.copy(sectores = sectoresRecibidos)
            }
        }
        viewModelScope.launch {
            repositorio.leerCultivos().collect { cultivosRecibidos ->
                _uiState.value = _uiState.value.copy(cultivos = cultivosRecibidos)
            }
        }
    }

    // Un reporte nuevo siempre empieza con el formulario vacío.
    fun abrirReporte() {
        limpiarFormulario()
        _uiState.value = _uiState.value.copy(formularioVisible = true)
    }

    fun elegirSector(codigoElegido: Int) {
        val formulario = _uiState.value
        // Al cambiar de sector se quita el cultivo si pertenecía a otro sector.
        val cultivoVigente = formulario.cultivoElegido?.takeIf { codigoCultivo ->
            formulario.cultivos.any { cultivo -> cultivo.id == codigoCultivo && cultivo.sectorId == codigoElegido }
        }
        _uiState.value = formulario.copy(sectorElegido = codigoElegido, cultivoElegido = cultivoVigente,
            errorSector = "", errorCultivo = "", aviso = "")
    }
    fun elegirCultivo(codigoElegido: Int?) {
        _uiState.value = _uiState.value.copy(cultivoElegido = codigoElegido, errorCultivo = "")
    }
    fun elegirTipo(tipoNuevo: TipoProblema) {
        _uiState.value = _uiState.value.copy(tipoElegido = tipoNuevo, errorTipo = "")
    }
    fun escribirDescripcion(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoDescripcion = textoNuevo, errorDescripcion = "")
    }
    fun adjuntarFoto(direccionElegida: String) {
        _uiState.value = _uiState.value.copy(direccionFoto = direccionElegida)
    }
    fun informar(mensajeNuevo: String) {
        _uiState.value = _uiState.value.copy(aviso = mensajeNuevo)
    }

    fun revisarReporte(): Boolean {
        val formulario = _uiState.value
        val errores = ValidadorProblemas.validarReporte(formulario.sectorElegido, formulario.sectores,
            formulario.cultivoElegido, formulario.cultivos, formulario.tipoElegido, formulario.textoDescripcion)
        _uiState.value = formulario.copy(errorSector = errores.sector, errorCultivo = errores.cultivo,
            errorTipo = errores.tipo, errorDescripcion = errores.descripcion)
        return errores.esValido
    }

    fun reportarProblema(cuentaAutora: Cuenta) {
        if (_uiState.value.guardando || !revisarReporte()) return
        val formulario = _uiState.value
        val codigoSector = formulario.sectorElegido ?: return
        val tipoProblema = formulario.tipoElegido ?: return
        val registroNuevo = Problema(sectorId = codigoSector, cultivoId = formulario.cultivoElegido,
            tipo = tipoProblema, descripcion = formulario.textoDescripcion.trim(),
            fechaReporte = LocalDateTime.now().withNano(0).toString(), autorId = cuentaAutora.codigo,
            fotografia = formulario.direccionFoto.ifBlank { null }, estado = EstadoProblema.INFORMADO)
        _uiState.value = formulario.copy(guardando = true)
        viewModelScope.launch {
            try {
                repositorio.registrarProblema(registroNuevo)
                limpiarFormulario()
                informar("Problema reportado. El docente lo revisará.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo reportar el problema.")
            } finally { _uiState.value = _uiState.value.copy(guardando = false) }
        }
    }

    fun iniciarSeguimiento(registroElegido: Problema) {
        _uiState.value = _uiState.value.copy(problemaEnSeguimiento = registroElegido,
            estadoSeguimiento = registroElegido.estado,
            textoSeguimiento = registroElegido.comentarioDocente ?: "", errorSeguimiento = "", aviso = "")
    }
    fun elegirEstadoSeguimiento(estadoNuevo: EstadoProblema) {
        _uiState.value = _uiState.value.copy(estadoSeguimiento = estadoNuevo)
    }
    fun escribirSeguimiento(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoSeguimiento = textoNuevo, errorSeguimiento = "")
    }
    fun cancelarSeguimiento() {
        _uiState.value = _uiState.value.copy(problemaEnSeguimiento = null, textoSeguimiento = "", errorSeguimiento = "")
    }

    fun guardarSeguimiento(cuentaResponsable: Cuenta) {
        val formulario = _uiState.value
        if (!cuentaResponsable.esDocente || formulario.guardando) return
        val problemaElegido = formulario.problemaEnSeguimiento ?: return
        val problemaComentario = ValidadorProblemas.validarSeguimiento(formulario.textoSeguimiento)
        if (problemaComentario.isNotEmpty()) {
            _uiState.value = formulario.copy(errorSeguimiento = problemaComentario)
            return
        }
        // La fecha de atención se registra la primera vez que el problema queda atendido o cerrado.
        val fechaCierre = if (formulario.estadoSeguimiento.resuelto)
            problemaElegido.fechaCierre ?: LocalDateTime.now().withNano(0).toString() else null
        _uiState.value = formulario.copy(guardando = true)
        viewModelScope.launch {
            try {
                repositorio.actualizarProblema(problemaElegido.copy(estado = formulario.estadoSeguimiento,
                    comentarioDocente = formulario.textoSeguimiento.trim().ifBlank { null }, fechaCierre = fechaCierre))
                cancelarSeguimiento()
                informar("Seguimiento guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo guardar el seguimiento.")
            } finally { _uiState.value = _uiState.value.copy(guardando = false) }
        }
    }

    fun limpiarFormulario() {
        val actual = _uiState.value
        _uiState.value = ProblemasUiState(problemas = actual.problemas,
            sectores = actual.sectores, cultivos = actual.cultivos)
    }
}
