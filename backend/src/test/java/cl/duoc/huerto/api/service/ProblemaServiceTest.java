package cl.duoc.huerto.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.duoc.huerto.api.controller.dto.SeguimientoProblemaRequest;
import cl.duoc.huerto.api.model.EstadoProblema;
import cl.duoc.huerto.api.model.Problema;
import cl.duoc.huerto.api.model.TipoProblema;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProblemaServiceTest {
    @Mock ProblemaRepository problemas;
    @Mock SectorService sectorService;
    @Mock CultivoService cultivoService;
    @InjectMocks ProblemaService service;

    @Test
    void unReporteNuevoQuedaInformado() {
        when(problemas.save(any(Problema.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        Problema nuevo = new Problema(1L, TipoProblema.PLAGA_SIMULADA, " Hojas con manchas ");
        nuevo.setEstado(EstadoProblema.CERRADO);

        Problema reportado = service.reportar(nuevo);

        verify(sectorService).buscar(1L);
        verify(cultivoService).revisarPertenencia(null, 1L);
        assertEquals("Hojas con manchas", reportado.getDescripcion());
        assertEquals(EstadoProblema.INFORMADO, reportado.getEstado());
        assertNotNull(reportado.getFechaHoraReporte());
    }

    @Test
    void alAtenderElProblemaSeGuardaLaFechaDeCierre() {
        Problema problema = new Problema(1L, TipoProblema.FALTA_AGUA, "Tierra seca");
        problema.setEstado(EstadoProblema.INFORMADO);
        when(problemas.findById(2L)).thenReturn(Optional.of(problema));
        when(problemas.save(any(Problema.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Problema enRevision = service.registrarSeguimiento(2L, new SeguimientoProblemaRequest(EstadoProblema.EN_REVISION, " "));
        assertNull(enRevision.getFechaCierre());
        assertNull(enRevision.getComentarioDocente());

        Problema atendido = service.registrarSeguimiento(2L, new SeguimientoProblemaRequest(EstadoProblema.ATENDIDO, "Se regó el sector"));
        assertNotNull(atendido.getFechaCierre());
        assertEquals("Se regó el sector", atendido.getComentarioDocente());
    }

    @Test
    void listarEliminarYBuscarInexistente() {
        when(problemas.findAll()).thenReturn(List.of(new Problema()));
        when(problemas.findById(2L)).thenReturn(Optional.of(new Problema()));
        when(problemas.findById(9L)).thenReturn(Optional.empty());

        assertEquals(1, service.listar().size());
        service.eliminar(2L);
        verify(problemas).deleteById(2L);
        assertThrows(ResponseStatusException.class, () -> service.buscar(9L));
    }
}
