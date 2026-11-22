package cl.duoc.huerto.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lo que envía el docente al aprobar o rechazar una tarea entregada.
public record RevisionTareaRequest(
        @NotNull(message = "Indica si la tarea se aprueba o se rechaza.") Boolean aprobada,
        @Size(max = 300) String comentario,
        @NotBlank(message = "Indica qué docente revisa la tarea.") String docenteId) { }
