package dharmaInventario.dharmaInventario.domain.model.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VentaItemEntity> items = new ArrayList<>();

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "es_mayorista")
    private boolean esMayorista;

    @Column(name = "descuento_adicional")
    private Double descuentoAdicional;

    @Column(name = "total")
    private Double total;

    @Column(name = "fecha")
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "distribuidor_id")
    private DistribuidorEntity distribuidor;
}