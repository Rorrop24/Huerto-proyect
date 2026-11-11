package cl.duoc.fotomotion.ui.labores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.fotomotion.data.repository.RepositorioHuerto
import cl.duoc.fotomotion.domain.model.CuentaHuerto
import cl.duoc.fotomotion.domain.model.LaborHuerto
import cl.duoc.fotomotion.domain.model.TipoCuenta
import cl.duoc.fotomotion.domain.validation.ValidacionesHuerto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class LaboresViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = RepositorioHuerto.desdeContexto(aplicacion)
    private val estadoMutable = MutableStateFlow(EstadoLabores())
    val estadoLabores = estadoMutable.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.leerLabores().collect { laboresRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(listaLabores = laboresRecibidas)
            }
        }
        viewModelScope.launch {
            repositorio.leerParcelas().collect { parcelasRecibidas ->
                estadoMutable.value = estadoMutable.value.copy(sectoresDisponibles = parcelasRecibidas)
            }
        }
    }

    fun escribirDetalle(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoDetalle = textoNuevo, errorDetalle = "", avisoLabores = "")
    }
    fun escribirFecha(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoFecha = textoNuevo, errorFecha = "")
    }
    fun elegirSectorTrabajo(codigoElegido: Int) {
        estadoMutable.value = estadoMutable.value.copy(sectorTrabajo = codigoElegido, errorSector = "")
    }

    fun revisarLabor(): Boolean {
        val formulario = estadoMutable.value
        val problemaDetalle = ValidacionesHuerto.revisarTexto(formulario.textoDetalle, 5, 150, "Explica qué hay que hacer.")
        val problemaFecha = ValidacionesHuerto.revisarFecha(formulario.textoFecha)
        val problemaSector = ValidacionesHuerto.revisarSector(formulario.sectorTrabajo, formulario.sectoresDisponibles,
            "Selecciona el sector de la tarea.")
        estadoMutable.value = formulario.copy(errorDetalle = problemaDetalle, errorFecha = problemaFecha, errorSector = problemaSector)
        return problemaDetalle.isEmpty() && problemaFecha.isEmpty() && problemaSector.isEmpty()
    }

    fun confirmarLabor(cuentaResponsable: CuentaHuerto) {
        if (cuentaResponsable.tipoCuenta != TipoCuenta.DOCENTE || estadoMutable.value.guardandoLabor || !revisarLabor()) return
        val formulario = estadoMutable.value
        val codigoSector = formulario.sectorTrabajo ?: return
        val laborAnterior = formulario.laborEnEdicion
        val registroNuevo = (laborAnterior ?: LaborHuerto(claseLabor = "CUIDADO", parcelaTrabajo = codigoSector,
            diaCreacion = LocalDate.now().toString(), autorLabor = cuentaResponsable.codigoCuenta,
            encargadoLabor = "USR-002", situacionLabor = "PENDIENTE")).copy(
                detalleLabor = formulario.textoDetalle.trim(), diaPlanificado = formulario.textoFecha,
                parcelaTrabajo = codigoSector)
        estadoMutable.value = formulario.copy(guardandoLabor = true)
        viewModelScope.launch {
            try {
                if (laborAnterior == null) repositorio.registrarLabor(registroNuevo)
                else repositorio.corregirLabor(registroNuevo)
                limpiarLabor()
                informarLabor("Tarea guardada.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarLabor("No se pudo guardar la tarea.")
            } finally { estadoMutable.value = estadoMutable.value.copy(guardandoLabor = false) }
        }
    }

    fun prepararLabor(registroElegido: LaborHuerto) {
        estadoMutable.value = estadoMutable.value.copy(laborEnEdicion = registroElegido,
            textoDetalle = registroElegido.detalleLabor, textoFecha = registroElegido.diaPlanificado,
            sectorTrabajo = registroElegido.parcelaTrabajo, errorDetalle = "", errorFecha = "", errorSector = "",
            avisoLabores = "Editando tarea.")
    }
    fun limpiarLabor() {
        estadoMutable.value = EstadoLabores(listaLabores = estadoMutable.value.listaLabores,
            sectoresDisponibles = estadoMutable.value.sectoresDisponibles)
    }
    fun iniciarEntrega(registroElegido: LaborHuerto) {
        estadoMutable.value = estadoMutable.value.copy(laborEnEntrega = registroElegido,
            direccionFoto = "", textoComentario = "", errorFoto = "", errorComentario = "", avisoLabores = "")
    }
    fun adjuntarImagen(direccionElegida: String) {
        estadoMutable.value = estadoMutable.value.copy(direccionFoto = direccionElegida, errorFoto = "")
    }
    fun escribirComentario(textoNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(textoComentario = textoNuevo, errorComentario = "")
    }
    fun informarLabor(mensajeNuevo: String) {
        estadoMutable.value = estadoMutable.value.copy(avisoLabores = mensajeNuevo)
    }

    fun revisarEntrega(): Boolean {
        val formulario = estadoMutable.value
        val problemaFoto = if (formulario.direccionFoto.isBlank()) "Adjunta una foto con cámara o galería." else ""
        val problemaComentario = ValidacionesHuerto.revisarTexto(formulario.textoComentario, 5, 300, "Cuenta qué hiciste en el huerto.")
        estadoMutable.value = formulario.copy(errorFoto = problemaFoto, errorComentario = problemaComentario)
        return problemaFoto.isEmpty() && problemaComentario.isEmpty()
    }

    fun entregarLabor(cuentaResponsable: CuentaHuerto) {
        if (cuentaResponsable.tipoCuenta != TipoCuenta.ALUMNO || estadoMutable.value.guardandoLabor || !revisarEntrega()) return
        val formulario = estadoMutable.value
        val laborElegida = formulario.laborEnEntrega ?: return
        estadoMutable.value = formulario.copy(guardandoLabor = true)
        viewModelScope.launch {
            try {
                repositorio.corregirLabor(laborElegida.copy(situacionLabor = "PENDIENTE_DE_VALIDACION",
                    imagenLabor = formulario.direccionFoto, notaEntrega = formulario.textoComentario.trim(),
                    encargadoLabor = cuentaResponsable.codigoCuenta, momentoEntrega = LocalDateTime.now().toString(),
                    notaRevision = null, momentoRevision = null, revisorLabor = null))
                limpiarLabor()
                informarLabor("Evidencia enviada al docente.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarLabor("No se pudo enviar la evidencia. Intenta nuevamente.")
            } finally { estadoMutable.value = estadoMutable.value.copy(guardandoLabor = false) }
        }
    }

    fun revisarTrabajo(registroElegido: LaborHuerto, aceptarTrabajo: Boolean, cuentaResponsable: CuentaHuerto) {
        if (cuentaResponsable.tipoCuenta != TipoCuenta.DOCENTE || estadoMutable.value.guardandoLabor) return
        estadoMutable.value = estadoMutable.value.copy(guardandoLabor = true)
        viewModelScope.launch {
            try {
                repositorio.corregirLabor(registroElegido.copy(situacionLabor = if (aceptarTrabajo) "VALIDADA" else "RECHAZADA",
                    notaRevision = if (aceptarTrabajo) "Trabajo aprobado." else "Revisa el trabajo y envía otra evidencia.",
                    revisorLabor = cuentaResponsable.codigoCuenta, momentoRevision = LocalDateTime.now().toString()))
                informarLabor(if (aceptarTrabajo) "Tarea aprobada." else "Tarea devuelta al estudiante.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarLabor("No se pudo revisar la tarea.")
            } finally { estadoMutable.value = estadoMutable.value.copy(guardandoLabor = false) }
        }
    }

    fun eliminarLabor(registroElegido: LaborHuerto) {
        viewModelScope.launch {
            try {
                repositorio.borrarLabor(registroElegido)
                if (estadoMutable.value.laborEnEdicion?.codigoLabor == registroElegido.codigoLabor) limpiarLabor()
                informarLabor("Tarea eliminada.")
            } catch (problema: Exception) {
                if (problema is CancellationException) throw problema
                informarLabor("No se pudo eliminar la tarea.")
            }
        }
    }
}
