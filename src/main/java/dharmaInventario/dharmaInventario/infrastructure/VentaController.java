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
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaEntity> listarVentas() {
        return ventaService.listarVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaEntity> obtenerVentaPorId(@PathVariable Long id) {
        VentaEntity venta = ventaService.obtenerVentaPorId(id);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(venta);
    }

    @PostMapping
    public ResponseEntity<VentaEntity> crearVenta(@RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.crearVenta(request));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
    }
}
