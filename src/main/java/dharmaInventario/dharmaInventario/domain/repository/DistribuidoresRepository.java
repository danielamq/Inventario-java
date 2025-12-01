package dharmaInventario.dharmaInventario.domain.repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistribuidoresRepository extends JpaRepository<DistribuidorEntity, Long> {
    List<DistribuidorEntity> findByNombreIgnoreCaseContaining(String nombre);
}
