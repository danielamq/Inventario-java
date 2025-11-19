package dharmaInventario.dharmaInventario.domain.model.DTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class VentaRequest {
    private Integer productoId;
    private Integer cantidad;
    private String nombreCliente;
    private boolean esMayorista;
    private Double descuentoAdicional;
    private Long distribuidorId;
}
