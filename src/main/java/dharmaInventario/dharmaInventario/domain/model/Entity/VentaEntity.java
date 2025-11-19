package dharmaInventario.dharmaInventario.domain.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "es_mayorista")
    private boolean esMayorista;

    @Column(name = "precio_usado")
    private Double precioUsado;

    @Column(name = "descuento_adicional")
    private Double descuentoAdicional;

    @Column(name = "fecha")
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @ManyToOne
    @JoinColumn(name = "distribuidor_id")
    private DistribuidorEntity distribuidor;
}