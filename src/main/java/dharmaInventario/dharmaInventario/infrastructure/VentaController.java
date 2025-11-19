package dharmaInventario.dharmaInventario.infrastructure;

import dharmaInventario.dharmaInventario.domain.model.DTO.ProductoRequest;
import dharmaInventario.dharmaInventario.domain.model.DTO.VentaRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.VentaEntity;
import dharmaInventario.dharmaInventario.domain.service.ProductoService;
import dharmaInventario.dharmaInventario.domain.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venta")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaEntity> listarVentas() {
        return ventaService.listarVentas();
    }

    @PostMapping
    public ResponseEntity<List<VentaEntity>> procesarVentas(@RequestBody List<VentaRequest> requests) {
        return ResponseEntity.ok(ventaService.procesarVentas(requests));
    }

    @GetMapping("/{id}")
    public VentaEntity obtenerVentaPorId(@PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
    }
}
