package cl.duoc.huerto.api.service;

import cl.duoc.huerto.api.controller.dto.SeguimientoProblemaRequest;
import cl.duoc.huerto.api.model.EstadoProblema;
import cl.duoc.huerto.api.model.Problema;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProblemaService {
    private final ProblemaRepository problemas;
    private final SectorService sectorService;
    private final CultivoService cultivoService;

    public ProblemaService(ProblemaRepository problemas, SectorService sectorService, CultivoService cultivoService) {
        this.problemas = problemas;
        this.sectorService = sectorService;
        this.cultivoService = cultivoService;
    }

    public List<Problema> listar() { return problemas.findAll(); }

    public Problema buscar(Long id) {
        return problemas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problema no encontrado."));
    }

    public Problema reportar(Problema problema) {
        sectorService.buscar(problema.getSectorId());
        cultivoService.revisarPertenencia(problema.getCultivoId(), problema.getSectorId());
        problema.setId(null);
        problema.setDescripcion(problema.getDescripcion().trim());
        problema.setEstado(EstadoProblema.INFORMADO);
        problema.setFechaHoraReporte(LocalDateTime.now().withNano(0));
        problema.setComentarioDocente(null);
        problema.setFechaCierre(null);
        return problemas.save(problema);
    }

    public Problema registrarSeguimiento(Long id, SeguimientoProblemaRequest seguimiento) {
        Problema problema = buscar(id);
        String comentario = seguimiento.comentario() == null ? "" : seguimiento.comentario().trim();
        problema.setEstado(seguimiento.estado());
        problema.setComentarioDocente(comentario.isEmpty() ? null : comentario);
        // La fecha de atención se guarda la primera vez que el problema queda resuelto.
        if (!seguimiento.estado().estaResuelto()) problema.setFechaCierre(null);
        else if (problema.getFechaCierre() == null) problema.setFechaCierre(LocalDateTime.now().withNano(0));
        return problemas.save(problema);
    }

    public void eliminar(Long id) {
        buscar(id);
        problemas.deleteById(id);
    }
}
