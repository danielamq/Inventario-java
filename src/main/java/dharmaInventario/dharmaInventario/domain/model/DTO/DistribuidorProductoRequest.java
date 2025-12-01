package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class DistribuidorProductoRequest {
    private Long id;
    private Long productoId;
    private Integer cantidadConsignada;
}
