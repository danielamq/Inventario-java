package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class VentaProductoRequest {
    private Long productoId;
    private Integer cantidad;
}
