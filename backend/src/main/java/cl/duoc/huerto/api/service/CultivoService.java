package cl.duoc.huerto.api.service;

import cl.duoc.huerto.api.model.Cultivo;
import cl.duoc.huerto.api.model.EstadoCultivo;
import cl.duoc.huerto.api.repository.CultivoRepository;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import cl.duoc.huerto.api.repository.TareaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CultivoService {
    private final CultivoRepository cultivos;
    private final TareaRepository tareas;
    private final ProblemaRepository problemas;
    private final SectorService sectorService;

    public CultivoService(CultivoRepository cultivos, TareaRepository tareas, ProblemaRepository problemas,
            SectorService sectorService) {
        this.cultivos = cultivos;
        this.tareas = tareas;
        this.problemas = problemas;
        this.sectorService = sectorService;
    }

    // Si se indica un sector, devuelve solo sus cultivos.
    public List<Cultivo> listar(Long sectorId) {
        return sectorId == null ? cultivos.findAll() : cultivos.findBySectorId(sectorId);
    }

    public Cultivo buscar(Long id) {
        return cultivos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultivo no encontrado."));
    }

    public Cultivo crear(Cultivo cultivo) {
        sectorService.buscarAsignable(cultivo.getSectorId());
        cultivo.setId(null);
        cultivo.setNombreComun(cultivo.getNombreComun().trim());
        if (cultivo.getEstado() == null) cultivo.setEstado(EstadoCultivo.SEMBRADO);
        if (cultivo.getFechaSiembra() == null) cultivo.setFechaSiembra(LocalDate.now());
        revisarFechas(cultivo);
        return cultivos.save(cultivo);
    }

    public Cultivo actualizar(Long id, Cultivo datos) {
        Cultivo actual = buscar(id);
        if (!datos.getSectorId().equals(actual.getSectorId())) sectorService.buscarAsignable(datos.getSectorId());
        actual.setNombreComun(datos.getNombreComun().trim());
        actual.setDescripcion(datos.getDescripcion());
        actual.setSectorId(datos.getSectorId());
        actual.setFechaSiembra(datos.getFechaSiembra());
        actual.setFechaTrasplante(datos.getFechaTrasplante());
        actual.setFechaEstimadaCosecha(datos.getFechaEstimadaCosecha());
        actual.setFrecuenciaRiegoDias(datos.getFrecuenciaRiegoDias());
        if (datos.getEstado() != null) actual.setEstado(datos.getEstado());
        actual.setObservaciones(datos.getObservaciones());
        revisarFechas(actual);
        return cultivos.save(actual);
    }

    public void eliminar(Long id) {
        buscar(id);
        if (tareas.countByCultivoId(id) > 0 || problemas.countByCultivoId(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Primero elimina las tareas y problemas de este cultivo.");
        }
        cultivos.deleteById(id);
    }

    // El cultivo es opcional en tareas y problemas, pero si se indica debe existir y pertenecer al sector.
    public void revisarPertenencia(Long cultivoId, Long sectorId) {
        if (cultivoId == null) return;
        Cultivo cultivo = cultivos.findById(cultivoId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cultivo ya no está disponible."));
        if (!cultivo.getSectorId().equals(sectorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cultivo no pertenece al sector elegido.");
        }
    }

    private void revisarFechas(Cultivo cultivo) {
        LocalDate siembra = cultivo.getFechaSiembra();
        if (siembra == null) return;
        if (cultivo.getFechaTrasplante() != null && cultivo.getFechaTrasplante().isBefore(siembra)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El trasplante no puede ser antes de la siembra.");
        }
        if (cultivo.getFechaEstimadaCosecha() != null && cultivo.getFechaEstimadaCosecha().isBefore(siembra)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cosecha no puede ser antes de la siembra.");
        }
    }
}
