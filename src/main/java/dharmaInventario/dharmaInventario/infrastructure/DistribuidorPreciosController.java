package dharmaInventario.dharmaInventario.infrastructure;

import dharmaInventario.dharmaInventario.domain.model.DTO.PrecioEspecialRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorPrecioEntity;
import dharmaInventario.dharmaInventario.domain.service.DistribuidorPreciosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribuidores/{distribuidorId}/precios")
@RequiredArgsConstructor
public class DistribuidorPreciosController {

    private final DistribuidorPreciosService distribuidorPreciosService;
    @GetMapping
    public ResponseEntity<List<DistribuidorPrecioEntity>> listar(@PathVariable Long distribuidorId) {

        List<DistribuidorPrecioEntity> precios =
                distribuidorPreciosService.obtenerPreciosPorDistribuidor(distribuidorId);

        return ResponseEntity.ok(precios);
    }

    @PostMapping
    public ResponseEntity<DistribuidorPrecioEntity> crearOActualizar(@PathVariable Long distribuidorId, @RequestBody PrecioEspecialRequest request) {

        DistribuidorPrecioEntity precio = distribuidorPreciosService.asignarOActualizarPrecio(distribuidorId, request);

        return ResponseEntity.ok(precio);
    }

    @GetMapping("/{productoId}/efectivo")
    public ResponseEntity<Double> obtenerPrecioEfectivo(@PathVariable Long distribuidorId, @PathVariable Long productoId) {

        Double precio = distribuidorPreciosService.obtenerPrecioEfectivo(distribuidorId, productoId);
        return ResponseEntity.ok(precio);
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long distribuidorId, @PathVariable Long productoId) {

        distribuidorPreciosService.eliminarPrecioEspecial(distribuidorId, productoId);
        return ResponseEntity.noContent().build();
    }
}
