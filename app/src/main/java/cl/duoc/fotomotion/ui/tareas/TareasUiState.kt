package cl.duoc.fotomotion.ui.tareas

import cl.duoc.fotomotion.domain.model.Cuenta
import cl.duoc.fotomotion.domain.model.Tarea
import cl.duoc.fotomotion.domain.model.Sector
import cl.duoc.fotomotion.domain.model.Cultivo
import cl.duoc.fotomotion.domain.model.TipoTarea
import java.time.LocalDate

data class TareasUiState(
    // Datos que vienen de la base
    val tareas: List<Tarea> = emptyList(),
    val sectores: List<Sector> = emptyList(),
    val cultivos: List<Cultivo> = emptyList(),
    val estudiantes: List<Cuenta> = emptyList(),
    // Formulario del docente
    val formularioVisible: Boolean = false,
    val textoDescripcion: String = "",
    val textoFecha: String = LocalDate.now().toString(),
    val sectorElegido: Int? = null,
    val cultivoElegido: Int? = null,
    val estudianteAsignado: String? = null,
    val tipoElegido: TipoTarea = TipoTarea.OTRA,
    val errorDescripcion: String = "",
    val errorFecha: String = "",
    val errorSector: String = "",
    val errorCultivo: String = "",
    val errorEstudiante: String = "",
    val tareaEnEdicion: Tarea? = null,
    // Entrega del estudiante
    val tareaEnEntrega: Tarea? = null,
    val direccionFoto: String = "",
    val textoComentario: String = "",
    val errorFoto: String = "",
    val errorComentario: String = "",
    // Mensajes y carga
    val aviso: String = "",
    val guardando: Boolean = false
)
