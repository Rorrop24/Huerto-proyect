package cl.duoc.huerto.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Lo que envía el estudiante al marcar una tarea como realizada.
public record EntregaTareaRequest(
        @NotBlank(message = "Adjunta una foto con cámara o galería.") String fotografia,
        @NotBlank(message = "Cuenta qué hiciste en el huerto.")
        @Size(min = 5, max = 300, message = "El comentario debe tener entre 5 y 300 caracteres.") String comentario,
        @NotBlank(message = "Indica qué estudiante entrega la tarea.") String estudianteId) { }
