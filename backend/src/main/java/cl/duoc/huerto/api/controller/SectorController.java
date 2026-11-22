package cl.duoc.huerto.api.controller;

import cl.duoc.huerto.api.model.Sector;
import cl.duoc.huerto.api.service.SectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sectores")
@Tag(name = "Sectores", description = "Zonas del huerto")
public class SectorController {
    private final SectorService service;

    public SectorController(SectorService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar sectores")
    public List<Sector> listar() { return service.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un sector por ID")
    public Sector buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un sector")
    public Sector crear(@Valid @RequestBody Sector sector) { return service.crear(sector); }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un sector")
    public Sector actualizar(@PathVariable Long id, @Valid @RequestBody Sector sector) { return service.actualizar(id, sector); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un sector sin cultivos, tareas ni problemas")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
