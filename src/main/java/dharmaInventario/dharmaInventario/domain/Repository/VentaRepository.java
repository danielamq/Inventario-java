package dharmaInventario.dharmaInventario.domain.Repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository  extends JpaRepository<VentaEntity,Long> {
}
