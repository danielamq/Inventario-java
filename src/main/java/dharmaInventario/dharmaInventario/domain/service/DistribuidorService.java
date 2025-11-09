package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.DistribuidorRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.Repository.DistribuidoresRepository;
import dharmaInventario.dharmaInventario.domain.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistribuidorService {

    private final DistribuidoresRepository distribuidorRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public DistribuidorEntity crearDistribuidor(DistribuidorRequest request) {
        ProductoEntity producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + request.getProductoId()));

        // Descontar del stock real y asignar a consignación
        if (producto.getCantidadReal() < request.getCantidadConsignada()) {
            throw new RuntimeException("No hay suficiente stock real para consignar al distribuidor.");
        }

        producto.setCantidadReal(producto.getCantidadReal() - request.getCantidadConsignada());
        producto.setCantidadConsignacion(
                (producto.getCantidadConsignacion() != null ? producto.getCantidadConsignacion() : 0)
                        + request.getCantidadConsignada()
        );

        DistribuidorEntity distribuidor = DistribuidorEntity.builder()
                .nombre(request.getNombre())
                .cantidadConsignada(request.getCantidadConsignada())
                .precioAsignado(producto.getPrecioMayorista())
                .producto(producto)
                .build();

        productoRepository.save(producto);
        return distribuidorRepository.save(distribuidor);
    }

    @Transactional
    public DistribuidorEntity actualizarDistribuidor(Long id, DistribuidorRequest request) {
        DistribuidorEntity distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado con ID: " + id));

        if (request.getNombre() != null) distribuidor.setNombre(request.getNombre());
        if (request.getCantidadConsignada() != null) {
            int diferencia = request.getCantidadConsignada() - distribuidor.getCantidadConsignada();

            ProductoEntity producto = distribuidor.getProducto();
            if (producto.getCantidadReal() < diferencia) {
                throw new RuntimeException("Stock insuficiente para aumentar consignación.");
            }
            else {
                distribuidor.setCantidadConsignada(request.getCantidadConsignada() + distribuidor.getCantidadConsignada());
            }

            producto.setCantidadReal(producto.getCantidadReal() - diferencia);
            producto.setCantidadConsignacion(producto.getCantidadConsignacion() + request.getCantidadConsignada());

            productoRepository.save(producto);
        }

        return distribuidorRepository.save(distribuidor);
    }

    @Transactional
    public void eliminarDistribuidor(Long id) {
        DistribuidorEntity distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado con ID: " + id));

        // Revertir el stock del producto
        ProductoEntity producto = distribuidor.getProducto();
        producto.setCantidadReal(producto.getCantidadReal() + distribuidor.getCantidadConsignada());
        producto.setCantidadConsignacion(producto.getCantidadConsignacion() - distribuidor.getCantidadConsignada());

        distribuidorRepository.delete(distribuidor);
        productoRepository.save(producto);
    }

    public DistribuidorEntity obtenerDistribuidorPorId(Long id) {
        return distribuidorRepository.findById(id).orElse(null);
    }


    public List<DistribuidorEntity> listarDistribuidores() {
        return distribuidorRepository.findAll();
    }
}
