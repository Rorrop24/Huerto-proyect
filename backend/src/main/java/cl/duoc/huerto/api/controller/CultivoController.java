package cl.duoc.huerto.api.controller;

import cl.duoc.huerto.api.model.Cultivo;
import cl.duoc.huerto.api.service.CultivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cultivos")
@Tag(name = "Cultivos", description = "Plantas sembradas en cada sector")
public class CultivoController {
    private final CultivoService service;

    public CultivoController(CultivoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar cultivos (opcional: ?sectorId=1)")
    public List<Cultivo> listar(@RequestParam(required = false) Long sectorId) { return service.listar(sectorId); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un cultivo por ID")
    public Cultivo buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cultivo")
    public Cultivo crear(@Valid @RequestBody Cultivo cultivo) { return service.crear(cultivo); }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cultivo")
    public Cultivo actualizar(@PathVariable Long id, @Valid @RequestBody Cultivo cultivo) { return service.actualizar(id, cultivo); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un cultivo sin tareas ni problemas")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
