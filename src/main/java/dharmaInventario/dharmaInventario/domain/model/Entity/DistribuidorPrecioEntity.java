package dharmaInventario.dharmaInventario.domain.model.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "distribuidor_precio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuidorPrecioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distribuidor_id", nullable = false)
    @JsonBackReference("precios")
    private DistribuidorEntity distribuidor;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "precio_especial")
    private Double precioEspecial;

}

