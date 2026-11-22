package cl.duoc.huerto.api.controller;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Devuelve todos los errores con el mismo formato para que la app pueda mostrar el mensaje.
@RestControllerAdvice
public class ManejadorErrores {

    public record RespuestaError(int status, String mensaje) { }

    // Reglas de negocio: sector fuera de uso, tarea ajena, no encontrado, etc.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RespuestaError> reglaDeNegocio(ResponseStatusException error) {
        int status = error.getStatusCode().value();
        return ResponseEntity.status(status).body(new RespuestaError(status, error.getReason()));
    }

    // Validaciones de los campos (@NotBlank, @Size, @Min...).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> datosInvalidos(MethodArgumentNotValidException error) {
        String mensaje = error.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getDefaultMessage())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
        return ResponseEntity.badRequest().body(new RespuestaError(HttpStatus.BAD_REQUEST.value(), mensaje));
    }
}
