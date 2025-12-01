package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DistribuidorRequest {

    private String nombre;
    private List<DistribuidorProductoRequest> productos;
    private List<Integer> productosAEliminar;

}

