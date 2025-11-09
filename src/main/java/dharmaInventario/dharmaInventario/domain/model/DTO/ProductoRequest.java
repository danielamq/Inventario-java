package dharmaInventario.dharmaInventario.domain.model.DTO;

import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private Double costo;
    private Double porcentajeMayorista;
    private Double porcentajeDetal;
    private Integer cantidadReal;
    private Integer cantidadConsignacion;
    private Double descuentoAdicional;
}