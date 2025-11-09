package dharmaInventario.dharmaInventario.domain.Repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistribuidoresRepository extends JpaRepository<DistribuidorEntity, Long> {
    List<DistribuidorEntity> findByNombreIgnoreCaseContaining(String nombre);
}
