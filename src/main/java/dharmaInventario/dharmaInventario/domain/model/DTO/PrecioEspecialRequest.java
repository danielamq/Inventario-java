package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class PrecioEspecialRequest {
    private Long productoId;
    private Double precioEspecial;
}
