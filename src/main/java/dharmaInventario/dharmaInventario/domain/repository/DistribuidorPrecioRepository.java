package dharmaInventario.dharmaInventario.domain.repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorPrecioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistribuidorPrecioRepository extends JpaRepository<DistribuidorPrecioEntity, Long> {

    Optional<DistribuidorPrecioEntity> findByDistribuidorIdAndProductoId(Long distribuidorId, Long productoId);

    List<DistribuidorPrecioEntity> findByDistribuidorId(Long distribuidorId);

    void deleteByDistribuidorIdAndProductoId(Long distribuidorId, Long productoId);
}
