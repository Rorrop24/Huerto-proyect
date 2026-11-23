package cl.duoc.fotomotion.ui.components

import cl.duoc.fotomotion.domain.model.EstadoCultivo
import cl.duoc.fotomotion.domain.model.EstadoProblema
import cl.duoc.fotomotion.domain.model.EstadoSector
import cl.duoc.fotomotion.domain.model.EstadoTarea

// Color con que se pinta la etiqueta de un estado (ver EtiquetaEstado).
enum class TonoEstado { NEUTRO, EXITO, ADVERTENCIA, ERROR }

// Qué tono le corresponde a cada estado. Si se agrega un estado nuevo, se define su color aquí.
fun EstadoSector.tono(): TonoEstado = when (this) {
    EstadoSector.DISPONIBLE, EstadoSector.EN_USO -> TonoEstado.EXITO
    EstadoSector.EN_MANTENIMIENTO -> TonoEstado.ADVERTENCIA
    EstadoSector.FUERA_DE_USO -> TonoEstado.ERROR
}

fun EstadoCultivo.tono(): TonoEstado = when (this) {
    EstadoCultivo.LISTO_PARA_COSECHAR, EstadoCultivo.COSECHADO -> TonoEstado.EXITO
    EstadoCultivo.CON_PROBLEMAS -> TonoEstado.ERROR
    EstadoCultivo.PLANIFICADO, EstadoCultivo.FINALIZADO -> TonoEstado.NEUTRO
    EstadoCultivo.SEMBRADO, EstadoCultivo.EN_CRECIMIENTO -> TonoEstado.ADVERTENCIA
}

fun EstadoTarea.tono(): TonoEstado = when (this) {
    EstadoTarea.PENDIENTE -> TonoEstado.NEUTRO
    EstadoTarea.PENDIENTE_DE_VALIDACION -> TonoEstado.ADVERTENCIA
    EstadoTarea.VALIDADA -> TonoEstado.EXITO
    EstadoTarea.RECHAZADA -> TonoEstado.ERROR
}

fun EstadoProblema.tono(): TonoEstado = when (this) {
    EstadoProblema.INFORMADO -> TonoEstado.ERROR
    EstadoProblema.EN_REVISION -> TonoEstado.ADVERTENCIA
    EstadoProblema.ATENDIDO, EstadoProblema.CERRADO -> TonoEstado.EXITO
}
