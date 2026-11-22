package cl.duoc.huerto.api.controller;

import cl.duoc.huerto.api.controller.dto.EntregaTareaRequest;
import cl.duoc.huerto.api.controller.dto.RevisionTareaRequest;
import cl.duoc.huerto.api.model.Tarea;
import cl.duoc.huerto.api.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tareas")
@Tag(name = "Tareas", description = "Trabajo asignado, entregado y revisado")
public class TareaController {
    private final TareaService service;

    public TareaController(TareaService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar tareas (opcional: ?responsableId=USR-002)")
    public List<Tarea> listar(@RequestParam(required = false) String responsableId) { return service.listar(responsableId); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una tarea por ID")
    public Tarea buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una tarea (docente)")
    public Tarea crear(@Valid @RequestBody Tarea tarea) { return service.crear(tarea); }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una tarea (docente)")
    public Tarea actualizar(@PathVariable Long id, @Valid @RequestBody Tarea tarea) { return service.actualizar(id, tarea); }

    @PutMapping("/{id}/entrega")
    @Operation(summary = "Entregar evidencia de una tarea (estudiante)")
    public Tarea entregar(@PathVariable Long id, @Valid @RequestBody EntregaTareaRequest entrega) { return service.entregar(id, entrega); }

    @PutMapping("/{id}/revision")
    @Operation(summary = "Aprobar o rechazar una tarea entregada (docente)")
    public Tarea revisar(@PathVariable Long id, @Valid @RequestBody RevisionTareaRequest revision) { return service.revisar(id, revision); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una tarea")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
