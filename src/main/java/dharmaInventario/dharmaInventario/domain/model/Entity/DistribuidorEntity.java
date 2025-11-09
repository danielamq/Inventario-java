package dharmaInventario.dharmaInventario.domain.model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "distribuidores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuidorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "cantidad_consignada")
    private Integer cantidadConsignada;

    @Column(name = "precio_asignado")
    private Double precioAsignado;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;
}