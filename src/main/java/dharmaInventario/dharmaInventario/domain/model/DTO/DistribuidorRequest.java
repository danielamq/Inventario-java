package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class DistribuidorRequest {

    private String nombre;
    private Long productoId;
    private Integer cantidadConsignada;

}

