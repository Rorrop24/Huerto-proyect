package cl.duoc.huerto.api.controller;

import cl.duoc.huerto.api.controller.dto.SeguimientoProblemaRequest;
import cl.duoc.huerto.api.model.Problema;
import cl.duoc.huerto.api.service.ProblemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problemas")
@Tag(name = "Problemas", description = "Reportes de problemas detectados en el huerto")
public class ProblemaController {
    private final ProblemaService service;

    public ProblemaController(ProblemaService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar problemas reportados")
    public List<Problema> listar() { return service.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un problema por ID")
    public Problema buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reportar un problema")
    public Problema reportar(@Valid @RequestBody Problema problema) { return service.reportar(problema); }

    @PutMapping("/{id}/seguimiento")
    @Operation(summary = "Cambiar el estado de un reporte y dejar un comentario (docente)")
    public Problema seguimiento(@PathVariable Long id, @Valid @RequestBody SeguimientoProblemaRequest seguimiento) {
        return service.registrarSeguimiento(id, seguimiento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un reporte")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
