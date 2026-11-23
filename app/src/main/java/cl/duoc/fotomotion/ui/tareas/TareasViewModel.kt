package cl.duoc.fotomotion.ui.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioCuentas
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.EstadoTarea
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.TipoTarea
import cl.duoc.fotomotion.domain.validation.ValidadorTareas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

// Recibe los repositorios desde afuera: en la app los entrega FabricaViewModels y en las pruebas unos con datos en memoria.
// Usa RepositorioCuentas solo para listar a los estudiantes a los que se asignan tareas.
class TareasViewModel(
    private val repositorio: RepositorioHuerto,
    private val repositorioCuentas: RepositorioCuentas
) : ViewModel() {
    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerTareas().collect { recibidas -> _uiState.value = _uiState.value.copy(tareas = recibidas) }
        }
        viewModelScope.launch {
            repositorio.leerSectores().collect { recibidas -> _uiState.value = _uiState.value.copy(sectores = recibidas) }
        }
        viewModelScope.launch {
            repositorio.leerCultivos().collect { recibidas -> _uiState.value = _uiState.value.copy(cultivos = recibidas) }
        }
        viewModelScope.launch {
            repositorioCuentas.leerEstudiantes().collect { recibidos -> _uiState.value = _uiState.value.copy(estudiantes = recibidos) }
        }
    }

    // ---------- Formulario del docente ----------
    fun abrirFormularioNuevo() {
        _uiState.value = formularioVacio().copy(formularioVisible = true)
    }
    fun escribirDescripcion(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoDescripcion = textoNuevo, errorDescripcion = "", aviso = "")
    }
    fun escribirFecha(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoFecha = textoNuevo, errorFecha = "")
    }
    fun elegirSector(codigoElegido: Int) {
        val formulario = _uiState.value
        // Al cambiar de sector se quita el cultivo si pertenecía a otro sector.
        val cultivoVigente = formulario.cultivoElegido?.takeIf { codigoCultivo ->
            formulario.cultivos.any { cultivo -> cultivo.id == codigoCultivo && cultivo.sectorId == codigoElegido }
        }
        _uiState.value = formulario.copy(sectorElegido = codigoElegido, cultivoElegido = cultivoVigente,
            errorSector = "", errorCultivo = "")
    }
    fun elegirCultivo(codigoElegido: Int?) {
        _uiState.value = _uiState.value.copy(cultivoElegido = codigoElegido, errorCultivo = "")
    }
    fun elegirTipo(tipoNuevo: TipoTarea) {
        _uiState.value = _uiState.value.copy(tipoElegido = tipoNuevo)
    }
    fun elegirEstudiante(estudiante: Cuenta) {
        _uiState.value = _uiState.value.copy(estudianteAsignado = estudiante.codigo, errorEstudiante = "")
    }

    fun guardarTarea(cuentaResponsable: Cuenta) {
        val formulario = _uiState.value
        if (!cuentaResponsable.esDocente || formulario.guardando) return
        val errores = ValidadorTareas.validarTarea(formulario.textoDescripcion, formulario.textoFecha, formulario.sectorElegido,
            formulario.sectores, formulario.cultivoElegido, formulario.cultivos,
            formulario.estudianteAsignado, formulario.estudiantes)
        _uiState.value = formulario.copy(errorDescripcion = errores.detalle, errorFecha = errores.fecha,
            errorSector = errores.sector, errorCultivo = errores.cultivo, errorEstudiante = errores.estudiante)
        if (!errores.esValido) return
        val codigoSector = formulario.sectorElegido ?: return
        val codigoEstudiante = formulario.estudianteAsignado ?: return
        val tareaAnterior = formulario.tareaEnEdicion
        val registroNuevo = (tareaAnterior ?: Tarea(sectorId = codigoSector, fechaCreacion = LocalDate.now().toString(),
            creadorId = cuentaResponsable.codigo)).copy(
            descripcion = formulario.textoDescripcion.trim(), fechaProgramada = formulario.textoFecha, sectorId = codigoSector,
            cultivoId = formulario.cultivoElegido, tipo = formulario.tipoElegido, responsableId = codigoEstudiante)
        guardar(mensajeExito = "Tarea guardada.", mensajeError = "No se pudo guardar la tarea.", limpiarAlTerminar = true) {
            if (tareaAnterior == null) repositorio.registrarTarea(registroNuevo) else repositorio.actualizarTarea(registroNuevo)
        }
    }

    fun editarTarea(registroElegido: Tarea) {
        _uiState.value = formularioVacio().copy(formularioVisible = true, tareaEnEdicion = registroElegido,
            textoDescripcion = registroElegido.descripcion, textoFecha = registroElegido.fechaProgramada,
            sectorElegido = registroElegido.sectorId, cultivoElegido = registroElegido.cultivoId,
            estudianteAsignado = registroElegido.responsableId.ifBlank { null }, tipoElegido = registroElegido.tipo,
            aviso = "Editando tarea.")
    }

    // Cancelar, terminar de guardar o salir deja todos los formularios vacíos.
    fun limpiarFormulario() {
        _uiState.value = formularioVacio()
    }

    // ---------- Entrega del estudiante ----------
    fun iniciarEntrega(registroElegido: Tarea) {
        _uiState.value = formularioVacio().copy(tareaEnEntrega = registroElegido)
    }
    fun adjuntarFoto(direccionElegida: String) {
        _uiState.value = _uiState.value.copy(direccionFoto = direccionElegida, errorFoto = "")
    }
    fun escribirComentario(textoNuevo: String) {
        _uiState.value = _uiState.value.copy(textoComentario = textoNuevo, errorComentario = "")
    }
    fun informar(mensajeNuevo: String) {
        _uiState.value = _uiState.value.copy(aviso = mensajeNuevo)
    }

    fun entregarTarea(cuentaResponsable: Cuenta) {
        val formulario = _uiState.value
        val tareaElegida = formulario.tareaEnEntrega ?: return
        if (cuentaResponsable.esDocente || formulario.guardando) return
        // Regla de negocio: solo el estudiante asignado entrega, y solo si la tarea está pendiente o fue rechazada.
        if (tareaElegida.responsableId != cuentaResponsable.codigo) {
            informar("Solo el estudiante asignado puede entregar esta tarea.")
            return
        }
        if (!tareaElegida.estado.admiteEntrega) {
            informar("Esta tarea ya fue entregada.")
            return
        }
        val errores = ValidadorTareas.validarEntrega(formulario.direccionFoto, formulario.textoComentario)
        _uiState.value = formulario.copy(errorFoto = errores.foto, errorComentario = errores.comentario)
        if (!errores.esValido) return
        val entregada = tareaElegida.copy(estado = EstadoTarea.PENDIENTE_DE_VALIDACION,
            fotografia = formulario.direccionFoto, comentarioEstudiante = formulario.textoComentario.trim(),
            fechaEntrega = LocalDateTime.now().withNano(0).toString(), comentarioDocente = null, fechaValidacion = null, validadorId = null)
        guardar(mensajeExito = "Evidencia enviada al docente.", mensajeError = "No se pudo enviar la evidencia. Intenta nuevamente.",
            limpiarAlTerminar = true) { repositorio.actualizarTarea(entregada) }
    }

    // ---------- Revisión del docente ----------
    fun revisarEntrega(registroElegido: Tarea, aceptarTrabajo: Boolean, cuentaResponsable: Cuenta) {
        if (!cuentaResponsable.esDocente || _uiState.value.guardando) return
        if (registroElegido.estado != EstadoTarea.PENDIENTE_DE_VALIDACION) return
        val revisada = registroElegido.copy(
            estado = if (aceptarTrabajo) EstadoTarea.VALIDADA else EstadoTarea.RECHAZADA,
            comentarioDocente = if (aceptarTrabajo) "Trabajo aprobado." else "Revisa el trabajo y envía otra evidencia.",
            validadorId = cuentaResponsable.codigo, fechaValidacion = LocalDateTime.now().withNano(0).toString())
        guardar(mensajeExito = if (aceptarTrabajo) "Tarea aprobada." else "Tarea devuelta al estudiante.",
            mensajeError = "No se pudo revisar la tarea.", limpiarAlTerminar = false) { repositorio.actualizarTarea(revisada) }
    }

    fun eliminarTarea(registroElegido: Tarea) {
        viewModelScope.launch {
            try {
                repositorio.eliminarTarea(registroElegido)
                if (_uiState.value.tareaEnEdicion?.id == registroElegido.id) limpiarFormulario()
                informar("Tarea eliminada.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar("No se pudo eliminar la tarea.")
            }
        }
    }

    // Ejecuta una operación de guardado mostrando la carga. Si falla, los datos escritos se conservan.
    private fun guardar(mensajeExito: String, mensajeError: String, limpiarAlTerminar: Boolean, operacion: suspend () -> Unit) {
        _uiState.value = _uiState.value.copy(guardando = true)
        viewModelScope.launch {
            try {
                operacion()
                if (limpiarAlTerminar) limpiarFormulario()
                informar(mensajeExito)
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informar(mensajeError)
            } finally { _uiState.value = _uiState.value.copy(guardando = false) }
        }
    }

    private fun formularioVacio(): TareasUiState {
        val actual = _uiState.value
        return TareasUiState(tareas = actual.tareas, sectores = actual.sectores,
            cultivos = actual.cultivos, estudiantes = actual.estudiantes)
    }
}
