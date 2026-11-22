package cl.duoc.huerto.api.model;

public enum EstadoProblema {
    INFORMADO, EN_REVISION, ATENDIDO, CERRADO;

    // Un problema atendido o cerrado ya no requiere acción del docente.
    public boolean estaResuelto() { return this == ATENDIDO || this == CERRADO; }
}
