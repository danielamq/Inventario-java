package dharmaInventario.dharmaInventario.domain.repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistribuidorProductoRepository extends JpaRepository<DistribuidorProductoEntity, Long> {
    Optional<DistribuidorProductoEntity> findByDistribuidorIdAndProductoId(Long distribuidorId, Long productoId);

}
