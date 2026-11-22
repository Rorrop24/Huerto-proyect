package cl.duoc.huerto.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sectores")
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Escribe el nombre del sector.")
    @Size(min = 3, max = 40, message = "El nombre debe tener entre 3 y 40 caracteres.")
    @Column(nullable = false, length = 40)
    private String nombre;

    @Size(max = 200)
    @Column(length = 200)
    private String descripcion;

    @NotBlank(message = "Escribe dónde está el sector.")
    @Size(min = 3, max = 80, message = "La ubicación debe tener entre 3 y 80 caracteres.")
    @Column(nullable = false, length = 80)
    private String ubicacion;

    // Se guarda como texto (EN_USO, FUERA_DE_USO...) para que la tabla sea fácil de leer.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoSector estado;

    @Column(name = "responsable_id", length = 20)
    private String responsableId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;

    public Sector() { }

    public Sector(String nombre, String ubicacion, EstadoSector estado) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public EstadoSector getEstado() { return estado; }
    public void setEstado(EstadoSector estado) { this.estado = estado; }
    public String getResponsableId() { return responsableId; }
    public void setResponsableId(String responsableId) { this.responsableId = responsableId; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDate getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDate fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
