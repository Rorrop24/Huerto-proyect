package cl.duoc.huerto.api.repository;

import cl.duoc.huerto.api.model.Problema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemaRepository extends JpaRepository<Problema, Long> {
    long countBySectorId(Long sectorId);
    long countByCultivoId(Long cultivoId);
}
