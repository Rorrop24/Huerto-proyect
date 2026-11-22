package cl.duoc.huerto.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.duoc.huerto.api.controller.dto.EntregaTareaRequest;
import cl.duoc.huerto.api.controller.dto.RevisionTareaRequest;
import cl.duoc.huerto.api.model.EstadoTarea;
import cl.duoc.huerto.api.model.Tarea;
import cl.duoc.huerto.api.model.TipoTarea;
import cl.duoc.huerto.api.repository.TareaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TareaServiceTest {
    @Mock TareaRepository tareas;
    @Mock SectorService sectorService;
    @Mock CultivoService cultivoService;
    @InjectMocks TareaService service;

    private Tarea tarea;

    @BeforeEach
    void prepararTarea() {
        tarea = new Tarea("Regar las lechugas", 1L, LocalDate.of(2026, 11, 25));
        tarea.setId(3L);
        tarea.setResponsableId("USR-002");
        tarea.setEstado(EstadoTarea.PENDIENTE);
        when(tareas.findById(3L)).thenReturn(Optional.of(tarea));
        when(tareas.save(any(Tarea.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void crearDejaLaTareaPendienteYRevisaSectorYCultivo() {
        Tarea nueva = new Tarea(" Regar tomates ", 1L, LocalDate.of(2026, 11, 30));
        nueva.setCultivoId(5L);
        nueva.setEstado(EstadoTarea.VALIDADA); // aunque la app envíe otro estado, siempre parte pendiente

        Tarea creada = service.crear(nueva);

        verify(sectorService).buscarAsignable(1L);
        verify(cultivoService).revisarPertenencia(5L, 1L);
        assertEquals("Regar tomates", creada.getDescripcion());
        assertEquals(TipoTarea.OTRA, creada.getTipo());
        assertEquals(EstadoTarea.PENDIENTE, creada.getEstado());
        assertEquals(LocalDate.now(), creada.getFechaCreacion());
    }

    @Test
    void listarFiltraPorEstudianteCuandoSeIndica() {
        when(tareas.findAll()).thenReturn(List.of(tarea, new Tarea()));
        when(tareas.findByResponsableId("USR-002")).thenReturn(List.of(tarea));

        assertEquals(2, service.listar(null).size());
        assertEquals(1, service.listar("USR-002").size());
    }

    @Test
    void actualizarNoCambiaElEstadoDeLaTarea() {
        Tarea datos = new Tarea("Regar y abonar las lechugas", 1L, LocalDate.of(2026, 12, 1));
        datos.setTipo(TipoTarea.RIEGO);
        datos.setEstado(EstadoTarea.VALIDADA);

        Tarea cambiada = service.actualizar(3L, datos);

        assertEquals("Regar y abonar las lechugas", cambiada.getDescripcion());
        assertEquals(TipoTarea.RIEGO, cambiada.getTipo());
        assertEquals(EstadoTarea.PENDIENTE, cambiada.getEstado());
    }

    @Test
    void elEstudianteAsignadoEntregaLaEvidencia() {
        Tarea entregada = service.entregar(3L, new EntregaTareaRequest("content://foto/1", " Regué todo ", "USR-002"));

        assertEquals(EstadoTarea.PENDIENTE_DE_VALIDACION, entregada.getEstado());
        assertEquals("Regué todo", entregada.getComentarioEstudiante());
        assertNotNull(entregada.getFechaHoraRealizacion());
    }

    @Test
    void otroEstudianteNoPuedeEntregarLaTarea() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.entregar(3L, new EntregaTareaRequest("content://foto/1", "Regué todo", "USR-003")));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
    }

    @Test
    void unaTareaValidadaNoSeVuelveAEntregar() {
        tarea.setEstado(EstadoTarea.VALIDADA);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.entregar(3L, new EntregaTareaRequest("content://foto/1", "Regué todo", "USR-002")));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void elDocenteApruebaUnaTareaEntregada() {
        tarea.setEstado(EstadoTarea.PENDIENTE_DE_VALIDACION);

        Tarea revisada = service.revisar(3L, new RevisionTareaRequest(true, "", "USR-001"));

        assertEquals(EstadoTarea.VALIDADA, revisada.getEstado());
        assertEquals("Trabajo aprobado.", revisada.getComentarioDocente());
        assertEquals("USR-001", revisada.getValidadorId());
    }

    @Test
    void paraRechazarHayQueExplicarQueCorregir() {
        tarea.setEstado(EstadoTarea.PENDIENTE_DE_VALIDACION);

        ResponseStatusException sinComentario = assertThrows(ResponseStatusException.class,
                () -> service.revisar(3L, new RevisionTareaRequest(false, " ", "USR-001")));
        assertEquals(HttpStatus.BAD_REQUEST, sinComentario.getStatusCode());

        Tarea rechazada = service.revisar(3L, new RevisionTareaRequest(false, "Falta regar el fondo", "USR-001"));
        assertEquals(EstadoTarea.RECHAZADA, rechazada.getEstado());
    }

    @Test
    void nadieValidaSuPropiaTareaNiUnaQueNoFueEntregada() {
        ResponseStatusException noEntregada = assertThrows(ResponseStatusException.class,
                () -> service.revisar(3L, new RevisionTareaRequest(true, "", "USR-001")));
        assertEquals(HttpStatus.CONFLICT, noEntregada.getStatusCode());

        tarea.setEstado(EstadoTarea.PENDIENTE_DE_VALIDACION);
        ResponseStatusException propia = assertThrows(ResponseStatusException.class,
                () -> service.revisar(3L, new RevisionTareaRequest(true, "", "USR-002")));
        assertEquals(HttpStatus.FORBIDDEN, propia.getStatusCode());
    }

    @Test
    void eliminarYBuscarInexistente() {
        service.eliminar(3L);
        verify(tareas).deleteById(3L);

        when(tareas.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.buscar(99L));
    }
}
