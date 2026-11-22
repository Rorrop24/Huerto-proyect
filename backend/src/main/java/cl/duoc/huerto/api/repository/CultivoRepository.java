package cl.duoc.huerto.api.repository;

import cl.duoc.huerto.api.model.Cultivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
    List<Cultivo> findBySectorId(Long sectorId);
    long countBySectorId(Long sectorId);
}
