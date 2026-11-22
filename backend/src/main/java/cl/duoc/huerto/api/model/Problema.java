package cl.duoc.huerto.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "problemas")
public class Problema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Selecciona el sector afectado.")
    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(name = "cultivo_id")
    private Long cultivoId;

    @NotNull(message = "Elige el tipo de problema.")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private TipoProblema tipo;

    @NotBlank(message = "Describe brevemente el problema.")
    @Size(min = 5, max = 200, message = "La descripción debe tener entre 5 y 200 caracteres.")
    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(name = "fecha_hora_reporte", nullable = false)
    private LocalDateTime fechaHoraReporte;

    @Column(name = "autor_id", length = 20)
    private String autorId;

    @Column(length = 500)
    private String fotografia;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoProblema estado;

    @Column(name = "comentario_docente", length = 200)
    private String comentarioDocente;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    public Problema() { }

    public Problema(Long sectorId, TipoProblema tipo, String descripcion) {
        this.sectorId = sectorId;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }
    public Long getCultivoId() { return cultivoId; }
    public void setCultivoId(Long cultivoId) { this.cultivoId = cultivoId; }
    public TipoProblema getTipo() { return tipo; }
    public void setTipo(TipoProblema tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaHoraReporte() { return fechaHoraReporte; }
    public void setFechaHoraReporte(LocalDateTime fechaHoraReporte) { this.fechaHoraReporte = fechaHoraReporte; }
    public String getAutorId() { return autorId; }
    public void setAutorId(String autorId) { this.autorId = autorId; }
    public String getFotografia() { return fotografia; }
    public void setFotografia(String fotografia) { this.fotografia = fotografia; }
    public EstadoProblema getEstado() { return estado; }
    public void setEstado(EstadoProblema estado) { this.estado = estado; }
    public String getComentarioDocente() { return comentarioDocente; }
    public void setComentarioDocente(String comentarioDocente) { this.comentarioDocente = comentarioDocente; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
}
