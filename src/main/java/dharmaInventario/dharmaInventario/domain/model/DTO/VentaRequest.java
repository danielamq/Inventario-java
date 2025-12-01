package dharmaInventario.dharmaInventario.domain.model.DTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VentaRequest {
    private String nombreCliente;
    private boolean esMayorista;
    private Double descuentoAdicional;
    private Long distribuidorId;
    private List<VentaProductoRequest > productos;
}
