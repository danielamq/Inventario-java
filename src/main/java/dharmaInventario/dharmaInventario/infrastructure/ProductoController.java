package dharmaInventario.dharmaInventario.infrastructure;

import dharmaInventario.dharmaInventario.domain.model.DTO.ProductoRequest;
import dharmaInventario.dharmaInventario.domain.model.DTO.StockRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoEntity> listarProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public ProductoEntity obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProductoEntity> crearProducto(@RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.crearProducto(request));
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest request) {
        ProductoEntity actualizado = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Integer> actualizarStock(
            @PathVariable Long id,
            @RequestBody Integer cantidadActualizar) {

        Integer stockActualizado = productoService.actualizarStock(id, cantidadActualizar);
        return ResponseEntity.ok(stockActualizado);
    }


}
