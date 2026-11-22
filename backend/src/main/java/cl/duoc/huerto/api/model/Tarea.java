package cl.duoc.huerto.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tareas")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private TipoTarea tipo;

    @NotBlank(message = "Explica qué hay que hacer.")
    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String descripcion;

    @NotNull(message = "Selecciona el sector de la tarea.")
    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(name = "cultivo_id")
    private Long cultivoId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @NotNull(message = "Escribe una fecha.")
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "responsable_id", length = 20)
    private String responsableId;

    @Column(name = "creador_id", length = 20)
    private String creadorId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoTarea estado;

    @Column(name = "fecha_hora_realizacion")
    private LocalDateTime fechaHoraRealizacion;

    @Column(length = 500)
    private String fotografia;

    @Column(name = "comentario_estudiante", length = 300)
    private String comentarioEstudiante;

    @Column(name = "comentario_docente", length = 300)
    private String comentarioDocente;

    @Column(name = "validador_id", length = 20)
    private String validadorId;

    @Column(name = "fecha_hora_validacion")
    private LocalDateTime fechaHoraValidacion;

    public Tarea() { }

    public Tarea(String descripcion, Long sectorId, LocalDate fechaProgramada) {
        this.descripcion = descripcion;
        this.sectorId = sectorId;
        this.fechaProgramada = fechaProgramada;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoTarea getTipo() { return tipo; }
    public void setTipo(TipoTarea tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }
    public Long getCultivoId() { return cultivoId; }
    public void setCultivoId(Long cultivoId) { this.cultivoId = cultivoId; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public String getResponsableId() { return responsableId; }
    public void setResponsableId(String responsableId) { this.responsableId = responsableId; }
    public String getCreadorId() { return creadorId; }
    public void setCreadorId(String creadorId) { this.creadorId = creadorId; }
    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }
    public LocalDateTime getFechaHoraRealizacion() { return fechaHoraRealizacion; }
    public void setFechaHoraRealizacion(LocalDateTime fechaHoraRealizacion) { this.fechaHoraRealizacion = fechaHoraRealizacion; }
    public String getFotografia() { return fotografia; }
    public void setFotografia(String fotografia) { this.fotografia = fotografia; }
    public String getComentarioEstudiante() { return comentarioEstudiante; }
    public void setComentarioEstudiante(String comentarioEstudiante) { this.comentarioEstudiante = comentarioEstudiante; }
    public String getComentarioDocente() { return comentarioDocente; }
    public void setComentarioDocente(String comentarioDocente) { this.comentarioDocente = comentarioDocente; }
    public String getValidadorId() { return validadorId; }
    public void setValidadorId(String validadorId) { this.validadorId = validadorId; }
    public LocalDateTime getFechaHoraValidacion() { return fechaHoraValidacion; }
    public void setFechaHoraValidacion(LocalDateTime fechaHoraValidacion) { this.fechaHoraValidacion = fechaHoraValidacion; }
}
