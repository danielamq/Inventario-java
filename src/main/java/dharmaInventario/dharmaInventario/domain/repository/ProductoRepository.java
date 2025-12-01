package dharmaInventario.dharmaInventario.domain.repository;

import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long>{

    List<ProductoEntity> findByNombreIgnoreCaseContaining(String nombre);
}
