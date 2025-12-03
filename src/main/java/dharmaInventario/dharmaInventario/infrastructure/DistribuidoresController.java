package dharmaInventario.dharmaInventario.infrastructure;

import dharmaInventario.dharmaInventario.domain.model.DTO.DistribuidorRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.service.DistribuidorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distribuidores")
public class DistribuidoresController {

    private final DistribuidorService distribuidorService;

    public DistribuidoresController(DistribuidorService distribuidorService) {
        this.distribuidorService = distribuidorService;
    }

    @PostMapping
    public ResponseEntity<DistribuidorEntity> crearDistribuidor(@RequestBody DistribuidorRequest request) {
        return ResponseEntity.ok(distribuidorService.crearDistribuidor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistribuidorEntity> actualizarDistribuidor(
            @PathVariable Long id, @RequestBody DistribuidorRequest request) {
        return ResponseEntity.ok(distribuidorService.actualizarDistribuidor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDistribuidor(@PathVariable Long id) {
        distribuidorService.eliminarDistribuidor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public DistribuidorEntity obtenerDistribuidorPorId(@PathVariable Long id) {
        return distribuidorService.obtenerDistribuidorPorId(id);
    }

    @GetMapping
    public List<DistribuidorEntity> listarDistribuidores() {
        return distribuidorService.listarDistribuidores();
    }

}
