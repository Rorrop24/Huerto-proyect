package cl.duoc.huerto.api.service;

import cl.duoc.huerto.api.model.EstadoSector;
import cl.duoc.huerto.api.model.Sector;
import cl.duoc.huerto.api.repository.CultivoRepository;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import cl.duoc.huerto.api.repository.SectorRepository;
import cl.duoc.huerto.api.repository.TareaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SectorService {
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final TareaRepository tareas;
    private final ProblemaRepository problemas;

    public SectorService(SectorRepository sectores, CultivoRepository cultivos, TareaRepository tareas,
            ProblemaRepository problemas) {
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.tareas = tareas;
        this.problemas = problemas;
    }

    public List<Sector> listar() { return sectores.findAll(); }

    public Sector buscar(Long id) {
        return sectores.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector no encontrado."));
    }

    // Un sector fuera de uso no puede recibir nuevos cultivos ni tareas.
    public Sector buscarAsignable(Long id) {
        Sector sector = buscar(id);
        if (sector.getEstado() == EstadoSector.FUERA_DE_USO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El sector está fuera de uso.");
        }
        return sector;
    }

    public Sector crear(Sector sector) {
        sector.setId(null);
        sector.setNombre(sector.getNombre().trim());
        sector.setUbicacion(sector.getUbicacion().trim());
        if (sector.getEstado() == null) sector.setEstado(EstadoSector.EN_USO);
        sector.setFechaCreacion(LocalDate.now());
        sector.setFechaActualizacion(LocalDate.now());
        return sectores.save(sector);
    }

    public Sector actualizar(Long id, Sector datos) {
        Sector actual = buscar(id);
        actual.setNombre(datos.getNombre().trim());
        actual.setDescripcion(datos.getDescripcion());
        actual.setUbicacion(datos.getUbicacion().trim());
        if (datos.getEstado() != null) actual.setEstado(datos.getEstado());
        if (datos.getResponsableId() != null) actual.setResponsableId(datos.getResponsableId());
        actual.setFechaActualizacion(LocalDate.now());
        return sectores.save(actual);
    }

    public void eliminar(Long id) {
        buscar(id);
        if (cultivos.countBySectorId(id) > 0 || tareas.countBySectorId(id) > 0 || problemas.countBySectorId(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Primero elimina los cultivos, tareas y problemas de este sector.");
        }
        sectores.deleteById(id);
    }
}
