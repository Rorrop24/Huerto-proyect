package cl.duoc.fotomotion.ui.problemas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.CuentaHuerto
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.ProblemaHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.model.TipoProblema
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ProblemasViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto.desdeContexto(aplicacion)
    private val estadoMutable = MutableStateFlow(EstadoProblemas())
    val estadoProblemas = estadoMutable.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerProblemas().collect { problemasRecibidos ->
                estadoMutable.value = estadoMutable.value.copy(listaProblemas = problemasRecibidos)
            }
        }
        viewModelScope.launch {
            repositorio.leerParcelas().collect { parcelasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(sectoresDisponibles = parcelasRecibidas)
            }
        }
        viewModelScope.launch {
            repositorio.leerSiembras().collect { siembrasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(cultivosDisponibles = siembrasRecibidas)
            }
        }
    }

    fun elegirSector(codigoElegido: Int) {
        val formulario = estadoMutable.value
        // Al cambiar de sector se quita el cultivo si pertenecía a otro sector.
        val cultivoVigente = formulario.cultivoElegido?.takeIf { codigoCultivo ->
            formulario.cultivosDisponibles.any { siembra -> siembra.codigoSiembra == codigoCultivo && siembra.parcelaAsociada == codigoElegido }
        }
        estadoMutable.value = formulario.copy(sectorElegido = codigoElegido, cultivoElegido = cultivoVigente,
            errorSector = "", errorCultivo = "", avisoProblemas = "")
    }
    fun elegirCultivo(codigoElegido: Int?) {
        estadoMutable.value = estadoMutable.value.copy(cultivoElegido = codigoElegido, errorCultivo = "")
    }
    fun elegirTipo(tipoNuevo: TipoProblema) {
        estadoMutable.value = estadoMutable.value.copy(tipoElegido = tipoNuevo, errorTipo = "")
    }
    fun escribirDescripcion(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoDescripcion = textoNuevo, errorDescripcion = "")
    }
    fun adjuntarFoto(direccionElegida: String) {
        estadoMutable.value = estadoMutable.value.copy(direccionFoto = direccionElegida)
    }
    fun informarProblema(mensajeNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(avisoProblemas = mensajeNuevo)
    }

    fun revisarReporte(): Boolean {
        val formulario = estadoMutable.value
        val problemaSector = ValidacionesHuerto.revisarSector(formulario.sectorElegido, formulario.sectoresDisponibles,
            "Selecciona el sector afectado.")
        val problemaCultivo = ValidacionesHuerto.revisarCultivo(formulario.cultivoElegido, formulario.sectorElegido,
            formulario.cultivosDisponibles)
        val problemaTipo = if (formulario.tipoElegido == null) "Elige el tipo de problema." else ""
        val problemaDescripcion = ValidacionesHuerto.revisarTexto(formulario.textoDescripcion, 5, 200,
            "Describe brevemente el problema.")
        estadoMutable.value = formulario.copy(errorSector = problemaSector, errorCultivo = problemaCultivo,
            errorTipo = problemaTipo, errorDescripcion = problemaDescripcion)
        return listOf(problemaSector, problemaCultivo, problemaTipo, problemaDescripcion).all { problema -> problema.isEmpty() }
    }

    fun reportarProblema(cuentaAutora: CuentaHuerto) {
        if (estadoMutable.value.guardandoProblema || !revisarReporte()) return
        val formulario = estadoMutable.value
        val codigoSector = formulario.sectorElegido ?: return
        val tipoProblema = formulario.tipoElegido ?: return
        val registroNuevo = ProblemaHuerto(parcelaAfectada = codigoSector, siembraAfectada = formulario.cultivoElegido,
            claseProblema = tipoProblema.name, detalleProblema = formulario.textoDescripcion.trim(),
            momentoReporte = LocalDateTime.now().withNano(0).toString(), autorReporte = cuentaAutora.codigoCuenta,
            imagenProblema = formulario.direccionFoto.ifBlank { null }, situacionProblema = EstadoProblema.INFORMADO.name)
        estadoMutable.value = formulario.copy(guardandoProblema = true)
        viewModelScope.launch {
            try {
                repositorio.registrarProblema(registroNuevo)
                limpiarFormulario()
                informarProblema("Problema reportado. El docente lo revisará.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarProblema("No se pudo reportar el problema.")
            } finally { estadoMutable.value = estadoMutable.value.copy(guardandoProblema = false) }
        }
    }

    fun iniciarSeguimiento(registroElegido: ProblemaHuerto) {
        estadoMutable.value = estadoMutable.value.copy(problemaEnSeguimiento = registroElegido,
            estadoSeguimiento = EstadoProblema.desde(registroElegido.situacionProblema),
            textoSeguimiento = registroElegido.notaSeguimiento ?: "", errorSeguimiento = "", avisoProblemas = "")
    }
    fun elegirEstadoSeguimiento(estadoNuevo: EstadoProblema) {
        estadoMutable.value = estadoMutable.value.copy(estadoSeguimiento = estadoNuevo)
    }
    fun escribirSeguimiento(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoSeguimiento = textoNuevo, errorSeguimiento = "")
    }
    fun cancelarSeguimiento() {
        estadoMutable.value = estadoMutable.value.copy(problemaEnSeguimiento = null, textoSeguimiento = "", errorSeguimiento = "")
    }

    fun guardarSeguimiento(cuentaResponsable: CuentaHuerto) {
        val formulario = estadoMutable.value
        if (cuentaResponsable.tipoCuenta != TipoCuenta.DOCENTE || formulario.guardandoProblema) return
        val problemaElegido = formulario.problemaEnSeguimiento ?: return
        val problemaComentario = if (formulario.textoSeguimiento.isBlank()) ""
            else ValidacionesHuerto.revisarTexto(formulario.textoSeguimiento, 3, 200, "")
        if (problemaComentario.isNotEmpty()) {
            estadoMutable.value = formulario.copy(errorSeguimiento = problemaComentario)
            return
        }
        // La fecha de atención se registra la primera vez que el problema queda atendido o cerrado.
        val momentoCierre = if (formulario.estadoSeguimiento.resuelto)
            problemaElegido.momentoCierre ?: LocalDateTime.now().withNano(0).toString() else null
        estadoMutable.value = formulario.copy(guardandoProblema = true)
        viewModelScope.launch {
            try {
                repositorio.corregirProblema(problemaElegido.copy(situacionProblema = formulario.estadoSeguimiento.name,
                    notaSeguimiento = formulario.textoSeguimiento.trim().ifBlank { null }, momentoCierre = momentoCierre))
                cancelarSeguimiento()
                informarProblema("Seguimiento guardado.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarProblema("No se pudo guardar el seguimiento.")
            } finally { estadoMutable.value = estadoMutable.value.copy(guardandoProblema = false) }
        }
    }

    fun limpiarFormulario() {
        val actual = estadoMutable.value
        estadoMutable.value = EstadoProblemas(listaProblemas = actual.listaProblemas,
            sectoresDisponibles = actual.sectoresDisponibles, cultivosDisponibles = actual.cultivosDisponibles)
    }
}
