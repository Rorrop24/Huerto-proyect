package cl.duoc.huerto.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.duoc.huerto.api.model.EstadoSector;
import cl.duoc.huerto.api.model.Sector;
import cl.duoc.huerto.api.repository.CultivoRepository;
import cl.duoc.huerto.api.repository.ProblemaRepository;
import cl.duoc.huerto.api.repository.SectorRepository;
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
class SectorServiceTest {
    @Mock SectorRepository sectores;
    @Mock CultivoRepository cultivos;
    @Mock TareaRepository tareas;
    @Mock ProblemaRepository problemas;
    @InjectMocks SectorService service;

    @Test
    void crearLimpiaTextosYAsignaValoresPorDefecto() {
        when(sectores.save(any(Sector.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Sector creado = service.crear(new Sector("  Invernadero ", " Patio norte ", null));

        assertEquals("Invernadero", creado.getNombre());
        assertEquals("Patio norte", creado.getUbicacion());
        assertEquals(EstadoSector.EN_USO, creado.getEstado());
        assertEquals(LocalDate.now(), creado.getFechaCreacion());
    }

    @Test
    void listarYBuscarDevuelvenLosSectoresGuardados() {
        Sector sector = new Sector("Invernadero", "Patio norte", EstadoSector.EN_USO);
        when(sectores.findAll()).thenReturn(List.of(sector));
        when(sectores.findById(1L)).thenReturn(Optional.of(sector));

        assertEquals(1, service.listar().size());
        assertSame(sector, service.buscar(1L));
    }

    @Test
    void buscarUnSectorInexistenteResponde404() {
        when(sectores.findById(9L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.buscar(9L));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void actualizarCambiaLosDatosYMantieneElEstadoSiNoSeEnvia() {
        Sector actual = new Sector("Invernadero", "Patio norte", EstadoSector.EN_MANTENIMIENTO);
        when(sectores.findById(1L)).thenReturn(Optional.of(actual));
        when(sectores.save(any(Sector.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Sector cambiado = service.actualizar(1L, new Sector("Invernadero grande", "Patio sur", null));

        assertEquals("Invernadero grande", cambiado.getNombre());
        assertEquals("Patio sur", cambiado.getUbicacion());
        assertEquals(EstadoSector.EN_MANTENIMIENTO, cambiado.getEstado());
        assertEquals(LocalDate.now(), cambiado.getFechaActualizacion());
    }

    @Test
    void unSectorFueraDeUsoNoAceptaAsignaciones() {
        when(sectores.findById(1L)).thenReturn(Optional.of(new Sector("Compostera", "Bodega", EstadoSector.FUERA_DE_USO)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.buscarAsignable(1L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void noSeEliminaUnSectorConCultivos() {
        when(sectores.findById(1L)).thenReturn(Optional.of(new Sector()));
        when(cultivos.countBySectorId(1L)).thenReturn(2L);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.eliminar(1L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(sectores, never()).deleteById(any());
    }

    @Test
    void eliminaUnSectorVacio() {
        when(sectores.findById(1L)).thenReturn(Optional.of(new Sector()));

        service.eliminar(1L);

        verify(sectores).deleteById(1L);
    }
}
