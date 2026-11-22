package cl.duoc.huerto.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "cultivos")
public class Cultivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Escribe el nombre del cultivo.")
    @Size(min = 3, max = 40, message = "El nombre debe tener entre 3 y 40 caracteres.")
    @Column(name = "nombre_comun", nullable = false, length = 40)
    private String nombreComun;

    @Size(max = 200)
    @Column(length = 200)
    private String descripcion;

    @NotNull(message = "Selecciona un sector.")
    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @Column(name = "fecha_siembra")
    private LocalDate fechaSiembra;

    @Column(name = "fecha_trasplante")
    private LocalDate fechaTrasplante;

    @Column(name = "fecha_estimada_cosecha")
    private LocalDate fechaEstimadaCosecha;

    @NotNull(message = "Indica cada cuántos días se riega.")
    @Min(value = 1, message = "Usa un número entre 1 y 30.")
    @Max(value = 30, message = "Usa un número entre 1 y 30.")
    @Column(name = "frecuencia_riego_dias", nullable = false)
    private Integer frecuenciaRiegoDias;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoCultivo estado;

    @Size(max = 300)
    @Column(length = 300)
    private String observaciones;

    public Cultivo() { }

    public Cultivo(String nombreComun, Long sectorId, Integer frecuenciaRiegoDias) {
        this.nombreComun = nombreComun;
        this.sectorId = sectorId;
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreComun() { return nombreComun; }
    public void setNombreComun(String nombreComun) { this.nombreComun = nombreComun; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }
    public LocalDate getFechaSiembra() { return fechaSiembra; }
    public void setFechaSiembra(LocalDate fechaSiembra) { this.fechaSiembra = fechaSiembra; }
    public LocalDate getFechaTrasplante() { return fechaTrasplante; }
    public void setFechaTrasplante(LocalDate fechaTrasplante) { this.fechaTrasplante = fechaTrasplante; }
    public LocalDate getFechaEstimadaCosecha() { return fechaEstimadaCosecha; }
    public void setFechaEstimadaCosecha(LocalDate fechaEstimadaCosecha) { this.fechaEstimadaCosecha = fechaEstimadaCosecha; }
    public Integer getFrecuenciaRiegoDias() { return frecuenciaRiegoDias; }
    public void setFrecuenciaRiegoDias(Integer frecuenciaRiegoDias) { this.frecuenciaRiegoDias = frecuenciaRiegoDias; }
    public EstadoCultivo getEstado() { return estado; }
    public void setEstado(EstadoCultivo estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
