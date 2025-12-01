package dharmaInventario.dharmaInventario.domain.model.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    private String nombre;

    @OneToMany(mappedBy = "distribuidor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("productos")
    @Builder.Default
    private List<DistribuidorProductoEntity> productos = new ArrayList<>();

    @OneToMany(mappedBy = "distribuidor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("precios")
    private List<DistribuidorPrecioEntity> preciosEspeciales = new ArrayList<>();
}