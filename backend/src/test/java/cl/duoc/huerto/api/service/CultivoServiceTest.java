package cl.duoc.huerto.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.duoc.huerto.api.model.Cultivo;
import cl.duoc.huerto.api.model.EstadoCultivo;
import cl.duoc.huerto.api.repository.CultivoRepository;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import cl.duoc.huerto.api.repository.TareaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CultivoServiceTest {
    @Mock CultivoRepository cultivos;
    @Mock TareaRepository tareas;
    @Mock ProblemaRepository problemas;
    @Mock SectorService sectorService;
    @InjectMocks CultivoService service;

    private Cultivo lechuga() {
        Cultivo cultivo = new Cultivo("Lechuga", 1L, 2);
        cultivo.setId(5L);
        return cultivo;
    }

    @Test
    void crearRevisaElSectorYAsignaValoresPorDefecto() {
        when(cultivos.save(any(Cultivo.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Cultivo creado = service.crear(new Cultivo(" Lechuga ", 1L, 2));

        verify(sectorService).buscarAsignable(1L);
        assertEquals("Lechuga", creado.getNombreComun());
        assertEquals(EstadoCultivo.SEMBRADO, creado.getEstado());
        assertEquals(LocalDate.now(), creado.getFechaSiembra());
    }

    @Test
    void laCosechaNoPuedeSerAntesDeLaSiembra() {
        Cultivo cultivo = new Cultivo("Lechuga", 1L, 2);
        cultivo.setFechaSiembra(LocalDate.of(2026, 5, 10));
        cultivo.setFechaEstimadaCosecha(LocalDate.of(2026, 5, 1));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.crear(cultivo));
        assertEquals("La cosecha no puede ser antes de la siembra.", error.getReason());
        verify(cultivos, never()).save(any());
    }

    @Test
    void elTrasplanteNoPuedeSerAntesDeLaSiembra() {
        Cultivo cultivo = new Cultivo("Lechuga", 1L, 2);
        cultivo.setFechaSiembra(LocalDate.of(2026, 5, 10));
        cultivo.setFechaTrasplante(LocalDate.of(2026, 5, 9));

        assertThrows(ResponseStatusException.class, () -> service.crear(cultivo));
    }

    @Test
    void listarFiltraPorSectorCuandoSeIndica() {
        when(cultivos.findAll()).thenReturn(List.of(lechuga(), lechuga()));
        when(cultivos.findBySectorId(1L)).thenReturn(List.of(lechuga()));

        assertEquals(2, service.listar(null).size());
        assertEquals(1, service.listar(1L).size());
    }

    @Test
    void actualizarRevisaElNuevoSectorSoloSiCambia() {
        when(cultivos.findById(5L)).thenReturn(Optional.of(lechuga()));
        when(cultivos.save(any(Cultivo.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        Cultivo datos = new Cultivo("Lechuga romana", 2L, 3);
        datos.setEstado(EstadoCultivo.EN_CRECIMIENTO);

        Cultivo cambiado = service.actualizar(5L, datos);

        verify(sectorService).buscarAsignable(2L);
        assertEquals("Lechuga romana", cambiado.getNombreComun());
        assertEquals(EstadoCultivo.EN_CRECIMIENTO, cambiado.getEstado());
        assertEquals(3, cambiado.getFrecuenciaRiegoDias());
    }

    @Test
    void noSeEliminaUnCultivoConTareas() {
        when(cultivos.findById(5L)).thenReturn(Optional.of(lechuga()));
        when(tareas.countByCultivoId(5L)).thenReturn(1L);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.eliminar(5L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void eliminaUnCultivoSinTareasNiProblemas() {
        when(cultivos.findById(5L)).thenReturn(Optional.of(lechuga()));

        service.eliminar(5L);

        verify(cultivos).deleteById(5L);
    }

    @Test
    void elCultivoDebePertenecerAlSector() {
        when(cultivos.findById(5L)).thenReturn(Optional.of(lechuga()));
        when(cultivos.findById(9L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.revisarPertenencia(null, 1L));
        assertDoesNotThrow(() -> service.revisarPertenencia(5L, 1L));
        ResponseStatusException otroSector = assertThrows(ResponseStatusException.class, () -> service.revisarPertenencia(5L, 2L));
        assertEquals("El cultivo no pertenece al sector elegido.", otroSector.getReason());
        assertThrows(ResponseStatusException.class, () -> service.revisarPertenencia(9L, 1L));
    }
}
