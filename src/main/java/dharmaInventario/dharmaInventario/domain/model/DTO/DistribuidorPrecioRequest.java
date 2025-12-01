package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class DistribuidorPrecioRequest {
    private Long productoId;
    private Double precioEspecial;
}
