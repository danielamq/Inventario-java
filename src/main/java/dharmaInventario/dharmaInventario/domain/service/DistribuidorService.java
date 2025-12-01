package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.DistribuidorProductoRequest;
import dharmaInventario.dharmaInventario.domain.model.DTO.DistribuidorRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorPrecioEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorProductoEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.repository.DistribuidorPrecioRepository;
import dharmaInventario.dharmaInventario.domain.repository.DistribuidoresRepository;
import dharmaInventario.dharmaInventario.domain.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistribuidorService {

    private final DistribuidoresRepository distribuidorRepository;
    private final ProductoRepository productoRepository;
    private final DistribuidorPrecioRepository distribuidorPrecioRepository;

    @Transactional
    public DistribuidorEntity crearDistribuidor(DistribuidorRequest request) {

        DistribuidorEntity distribuidor = DistribuidorEntity.builder()
                .nombre(request.getNombre())
                .productos(new ArrayList<>())
                .build();

        distribuidor = distribuidorRepository.save(distribuidor);

        for (DistribuidorProductoRequest item : request.getProductos()) {

            ProductoEntity producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getCantidadReal() < item.getCantidadConsignada()) {
                throw new RuntimeException("Stock insuficiente para consignación");
            }

            // DESCONTAR REAL → SUMAR CONSIGNACIÓN
            producto.setCantidadReal(producto.getCantidadReal() - item.getCantidadConsignada());
            producto.setCantidadConsignacion(producto.getCantidadConsignacion() + item.getCantidadConsignada());

            productoRepository.save(producto);

            Optional<DistribuidorPrecioEntity> precioEspecial =
                    distribuidorPrecioRepository.findByDistribuidorIdAndProductoId(
                            distribuidor.getId(),
                            item.getProductoId());

            double precioUsado = precioEspecial.map(DistribuidorPrecioEntity::getPrecioEspecial)
                    .orElse(producto.getPrecioMayorista());

            String tipoPrecio = precioEspecial.isPresent() ? "Especial" : "Mayorista";
            DistribuidorProductoEntity distribuidorProducto = DistribuidorProductoEntity.builder()
                    .distribuidor(distribuidor)
                    .producto(producto)
                    .cantidadConsignada(item.getCantidadConsignada())
                    .precioAsignado(precioUsado)
                    .tipoPrecio(tipoPrecio)
                    .build();

            distribuidor.getProductos().add(distribuidorProducto);
        }

        return distribuidorRepository.save(distribuidor);
    }

    @Transactional
    public DistribuidorEntity actualizarDistribuidor(Long id, DistribuidorRequest request) {

        DistribuidorEntity distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado"));

        // Actualizar nombre si se proporciona
        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            distribuidor.setNombre(request.getNombre());
        }

        // Map para encontrar rápidamente productos existentes por ID
        Map<Long, DistribuidorProductoEntity> productosExistentesMap = distribuidor.getProductos().stream()
                .collect(Collectors.toMap(dp -> dp.getProducto().getId(), dp -> dp));

        for (DistribuidorProductoRequest item : request.getProductos()) {

            ProductoEntity producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            DistribuidorProductoEntity productoExistente = productosExistentesMap.get(item.getProductoId());

            if (productoExistente != null) {
                // Producto existente: ajustar cantidades
                int diferencia = item.getCantidadConsignada() - productoExistente.getCantidadConsignada();

                if (diferencia > 0) {
                    if (producto.getCantidadReal() < diferencia) {
                        throw new RuntimeException("Stock insuficiente para aumentar consignación");
                    }
                    producto.setCantidadReal(producto.getCantidadReal() - diferencia);
                    producto.setCantidadConsignacion(producto.getCantidadConsignacion() + diferencia);
                } else if (diferencia < 0) {
                    producto.setCantidadReal(producto.getCantidadReal() + Math.abs(diferencia));
                    producto.setCantidadConsignacion(producto.getCantidadConsignacion() - Math.abs(diferencia));
                }

                productoRepository.save(producto);

                // Actualizar datos del distribuidor-producto
                productoExistente.setCantidadConsignada(item.getCantidadConsignada());
                // Actualizar precio si quieres recalcular
                Optional<DistribuidorPrecioEntity> precioEspecial =
                        distribuidorPrecioRepository.findByDistribuidorIdAndProductoId(distribuidor.getId(), item.getProductoId());
                double precioUsado = precioEspecial.map(DistribuidorPrecioEntity::getPrecioEspecial)
                        .orElse(producto.getPrecioMayorista());
                productoExistente.setPrecioAsignado(precioUsado);
                productoExistente.setTipoPrecio(precioEspecial.isPresent() ? "Especial" : "Mayorista");

            } else {
                // Producto nuevo: agregar
                if (producto.getCantidadReal() < item.getCantidadConsignada()) {
                    throw new RuntimeException("Stock insuficiente para consignar este producto.");
                }

                producto.setCantidadReal(producto.getCantidadReal() - item.getCantidadConsignada());
                producto.setCantidadConsignacion(producto.getCantidadConsignacion() + item.getCantidadConsignada());
                productoRepository.save(producto);

                Optional<DistribuidorPrecioEntity> precioEspecial =
                        distribuidorPrecioRepository.findByDistribuidorIdAndProductoId(distribuidor.getId(), item.getProductoId());
                double precioUsado = precioEspecial.map(DistribuidorPrecioEntity::getPrecioEspecial)
                        .orElse(producto.getPrecioMayorista());

                DistribuidorProductoEntity nuevo = DistribuidorProductoEntity.builder()
                        .distribuidor(distribuidor)
                        .producto(producto)
                        .cantidadConsignada(item.getCantidadConsignada())
                        .precioAsignado(precioUsado)
                        .tipoPrecio(precioEspecial.isPresent() ? "Especial" : "Mayorista")
                        .build();

                distribuidor.getProductos().add(nuevo);
            }
        }
        if (request.getProductosAEliminar() != null && !request.getProductosAEliminar().isEmpty()) {
            distribuidor.getProductos().removeIf(dp -> {
                Long productoId = dp.getProducto().getId();
                boolean debeEliminar = request.getProductosAEliminar().stream()
                        .map(Number::longValue)
                        .anyMatch(eliminarId -> eliminarId.equals(productoId));

                if (debeEliminar) {
                    ProductoEntity producto = dp.getProducto();
                    producto.setCantidadConsignacion(producto.getCantidadConsignacion() - dp.getCantidadConsignada());
                    productoRepository.save(producto);
                    return true;
                }
                return false;
            });
        }
        return distribuidorRepository.save(distribuidor);
    }

    @Transactional
    public void eliminarDistribuidor(Long id) {

        DistribuidorEntity distribuidor = distribuidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado"));

        for (DistribuidorProductoEntity dp : distribuidor.getProductos()) {
            ProductoEntity producto = dp.getProducto();

            producto.setCantidadReal(producto.getCantidadReal() + dp.getCantidadConsignada());
            producto.setCantidadConsignacion(producto.getCantidadConsignacion() - dp.getCantidadConsignada());

            productoRepository.save(producto);
        }

        distribuidorRepository.delete(distribuidor);
    }

    public DistribuidorEntity obtenerDistribuidorPorId(Long id) {
        return distribuidorRepository.findById(id).orElse(null);
    }


    public List<DistribuidorEntity> listarDistribuidores() {
        return distribuidorRepository.findAll();
    }
}
