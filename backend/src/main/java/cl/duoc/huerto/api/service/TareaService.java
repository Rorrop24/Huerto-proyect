package cl.duoc.huerto.api.service;

import cl.duoc.huerto.api.controller.dto.EntregaTareaRequest;
import cl.duoc.huerto.api.controller.dto.RevisionTareaRequest;
import cl.duoc.huerto.api.model.EstadoTarea;
import cl.duoc.huerto.api.model.Tarea;
import cl.duoc.huerto.api.model.TipoTarea;
import cl.duoc.huerto.api.repository.TareaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TareaService {
    // Estados desde los que el estudiante puede entregar evidencia.
    private static final Set<EstadoTarea> ENTREGABLES =
            EnumSet.of(EstadoTarea.PENDIENTE, EstadoTarea.EN_DESARROLLO, EstadoTarea.RECHAZADA, EstadoTarea.ATRASADA);

    private final TareaRepository tareas;
    private final SectorService sectorService;
    private final CultivoService cultivoService;

    public TareaService(TareaRepository tareas, SectorService sectorService, CultivoService cultivoService) {
        this.tareas = tareas;
        this.sectorService = sectorService;
        this.cultivoService = cultivoService;
    }

    // Si se indica un estudiante, devuelve solo las tareas asignadas a él.
    public List<Tarea> listar(String responsableId) {
        return responsableId == null ? tareas.findAll() : tareas.findByResponsableId(responsableId);
    }

    public Tarea buscar(Long id) {
        return tareas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada."));
    }

    public Tarea crear(Tarea tarea) {
        sectorService.buscarAsignable(tarea.getSectorId());
        cultivoService.revisarPertenencia(tarea.getCultivoId(), tarea.getSectorId());
        tarea.setId(null);
        tarea.setDescripcion(tarea.getDescripcion().trim());
        if (tarea.getTipo() == null) tarea.setTipo(TipoTarea.OTRA);
        tarea.setEstado(EstadoTarea.PENDIENTE);
        tarea.setFechaCreacion(LocalDate.now());
        limpiarEntregaYRevision(tarea);
        return tareas.save(tarea);
    }

    // Edición del docente: no cambia el estado ni la evidencia entregada.
    public Tarea actualizar(Long id, Tarea datos) {
        Tarea actual = buscar(id);
        if (!datos.getSectorId().equals(actual.getSectorId())) sectorService.buscarAsignable(datos.getSectorId());
        cultivoService.revisarPertenencia(datos.getCultivoId(), datos.getSectorId());
        actual.setDescripcion(datos.getDescripcion().trim());
        if (datos.getTipo() != null) actual.setTipo(datos.getTipo());
        actual.setSectorId(datos.getSectorId());
        actual.setCultivoId(datos.getCultivoId());
        actual.setFechaProgramada(datos.getFechaProgramada());
        if (datos.getResponsableId() != null) actual.setResponsableId(datos.getResponsableId());
        return tareas.save(actual);
    }

    public Tarea entregar(Long id, EntregaTareaRequest entrega) {
        Tarea tarea = buscar(id);
        if (!ENTREGABLES.contains(tarea.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La tarea no se puede entregar en su estado actual.");
        }
        if (tarea.getResponsableId() != null && !tarea.getResponsableId().equals(entrega.estudianteId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el estudiante asignado puede entregar esta tarea.");
        }
        tarea.setEstado(EstadoTarea.PENDIENTE_DE_VALIDACION);
        tarea.setFotografia(entrega.fotografia());
        tarea.setComentarioEstudiante(entrega.comentario().trim());
        tarea.setResponsableId(entrega.estudianteId());
        tarea.setFechaHoraRealizacion(LocalDateTime.now().withNano(0));
        tarea.setComentarioDocente(null);
        tarea.setValidadorId(null);
        tarea.setFechaHoraValidacion(null);
        return tareas.save(tarea);
    }

    public Tarea revisar(Long id, RevisionTareaRequest revision) {
        Tarea tarea = buscar(id);
        if (tarea.getEstado() != EstadoTarea.PENDIENTE_DE_VALIDACION) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden revisar tareas pendientes de validación.");
        }
        if (revision.docenteId().equals(tarea.getResponsableId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nadie puede validar su propia tarea.");
        }
        boolean aprobada = revision.aprobada();
        String comentario = revision.comentario() == null ? "" : revision.comentario().trim();
        if (!aprobada && comentario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica qué debe corregir el estudiante.");
        }
        tarea.setEstado(aprobada ? EstadoTarea.VALIDADA : EstadoTarea.RECHAZADA);
        tarea.setComentarioDocente(comentario.isEmpty() ? "Trabajo aprobado." : comentario);
        tarea.setValidadorId(revision.docenteId());
        tarea.setFechaHoraValidacion(LocalDateTime.now().withNano(0));
        return tareas.save(tarea);
    }

    public void eliminar(Long id) {
        buscar(id);
        tareas.deleteById(id);
    }

    private void limpiarEntregaYRevision(Tarea tarea) {
        tarea.setFechaHoraRealizacion(null);
        tarea.setFotografia(null);
        tarea.setComentarioEstudiante(null);
        tarea.setComentarioDocente(null);
        tarea.setValidadorId(null);
        tarea.setFechaHoraValidacion(null);
    }
}
