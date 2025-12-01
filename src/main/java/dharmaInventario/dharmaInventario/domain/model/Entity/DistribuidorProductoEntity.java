package dharmaInventario.dharmaInventario.domain.model.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "distribuidor_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuidorProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distribuidor_id", nullable = false)
    @JsonBackReference("productos")
    private DistribuidorEntity distribuidor;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "cantidad_consignada")
    private Integer cantidadConsignada;

    @Column(name = "precio_asignado")
    private Double precioAsignado;

    @Column(name = "tipo_precio")
    private String tipoPrecio;
}