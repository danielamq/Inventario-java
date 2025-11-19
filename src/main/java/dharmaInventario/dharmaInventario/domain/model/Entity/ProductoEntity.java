package dharmaInventario.dharmaInventario.domain.model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "costo")
    private Double costo;

    @Column(name = "cantidad_real")
    private Integer cantidadReal;

    @Column(name = "cantidad_consignacion")
    private Integer cantidadConsignacion;

    @Column(name = "precio_mayorista")
    private Double precioMayorista;

    @Column(name = "precio_detal")
    private Double precioDetal;

    @Column(name = "porcentaje_mayorista")
    private Double porcentajeMayorista;

    @Column(name = "porcentaje_detal")
    private Double porcentajeDetal;
}