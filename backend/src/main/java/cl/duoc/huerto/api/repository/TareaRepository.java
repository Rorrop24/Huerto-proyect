package cl.duoc.huerto.api.repository;

import cl.duoc.huerto.api.model.Tarea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByResponsableId(String responsableId);
    long countBySectorId(Long sectorId);
    long countByCultivoId(Long cultivoId);
}
