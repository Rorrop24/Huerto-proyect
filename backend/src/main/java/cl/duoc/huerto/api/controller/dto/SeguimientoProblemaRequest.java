package cl.duoc.huerto.api.controller.dto;

import cl.duoc.huerto.api.model.EstadoProblema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lo que envía el docente al dar seguimiento a un problema reportado.
public record SeguimientoProblemaRequest(
        @NotNull(message = "Elige el estado del reporte.") EstadoProblema estado,
        @Size(max = 200, message = "El comentario puede tener hasta 200 caracteres.") String comentario) { }
