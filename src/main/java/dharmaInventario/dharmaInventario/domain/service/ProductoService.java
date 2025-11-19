package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.ProductoRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.Repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoEntity> listarProductos() {
        return productoRepository.findAll();
    }

    public ProductoEntity obtenerProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional
    public ProductoEntity crearProducto(ProductoRequest request) {
        if (request.getNombre() == null || request.getCosto() == null) {
            throw new IllegalArgumentException("El producto debe tener nombre y costo");
        }

        ProductoEntity producto = new ProductoEntity();
        producto.setNombre(request.getNombre());
        producto.setCosto(request.getCosto());
        producto.setCantidadReal(request.getCantidadReal());
        producto.setCantidadConsignacion(request.getCantidadConsignacion());
        producto.setPorcentajeDetal(request.getPorcentajeDetal());
        producto.setPorcentajeMayorista(request.getPorcentajeMayorista());

        // Calcular precios solo si hay porcentajes
        if (request.getPorcentajeMayorista() != null) {
            producto.setPrecioMayorista(
                    request.getCosto() * (1 + (request.getPorcentajeMayorista() / 100))
            );
        }

        if (request.getPorcentajeDetal() != null) {
            producto.setPrecioDetal(
                    request.getCosto() * (1 + (request.getPorcentajeDetal() / 100))
            );
        }

        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    @Transactional
    public ProductoEntity actualizarProducto(Long id, ProductoRequest request) {
        // Buscar el producto existente
        ProductoEntity productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID " + id + " no encontrado."));

        // Actualizar solo los campos que vengan en el request (sin perder los existentes)
        if (request.getNombre() != null) productoExistente.setNombre(request.getNombre());
        if (request.getCosto() != null) productoExistente.setCosto(request.getCosto());
        if (request.getCantidadReal() != null) productoExistente.setCantidadReal(request.getCantidadReal());
        if (request.getCantidadConsignacion() != null) {
            productoExistente.setCantidadConsignacion(request.getCantidadConsignacion());
            productoExistente.setCantidadReal(request.getCantidadConsignacion() - productoExistente.getCantidadReal());
        }
        if (request.getPorcentajeDetal() != null) productoExistente.setPorcentajeDetal(request.getPorcentajeDetal());
        if (request.getPorcentajeMayorista() != null)
            productoExistente.setPorcentajeMayorista(request.getPorcentajeMayorista());

        // Recalcular precios solo si hay cambios de costo o porcentajes
        if (productoExistente.getCosto() != null) {
            if (productoExistente.getPorcentajeMayorista() != null) {
                productoExistente.setPrecioMayorista(
                        productoExistente.getCosto() * (1 + (productoExistente.getPorcentajeMayorista() / 100))
                );
            }

            if (productoExistente.getPorcentajeDetal() != null) {
                productoExistente.setPrecioDetal(
                        productoExistente.getCosto() * (1 + (productoExistente.getPorcentajeDetal() / 100))
                );
            }
        }

        return productoRepository.save(productoExistente);
    }

    public Integer actualizarStock(Long id, Integer cantidadActualizada) {
        Optional<ProductoEntity> productoExistente = productoRepository.findById(id);

        if (productoExistente.isPresent()) {
            ProductoEntity producto = productoExistente.get();
            producto.setCantidadReal(cantidadActualizada + producto.getCantidadReal());
            productoRepository.save(producto);
            return producto.getCantidadReal();
        } else {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
    }
}