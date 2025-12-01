package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.PrecioEspecialRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorPrecioEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.repository.DistribuidorPrecioRepository;
import dharmaInventario.dharmaInventario.domain.repository.DistribuidoresRepository;
import dharmaInventario.dharmaInventario.domain.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistribuidorPreciosService {

    private final DistribuidoresRepository distribuidorRepository;
    private final ProductoRepository productoRepository;
    private final DistribuidorPrecioRepository distribuidorPrecioRepository;

    public List<DistribuidorPrecioEntity> obtenerPreciosPorDistribuidor(Long distribuidorId) {
        return distribuidorPrecioRepository.findByDistribuidorId(distribuidorId);
    }

    @Transactional
    public DistribuidorPrecioEntity asignarOActualizarPrecio(Long distribuidorId, PrecioEspecialRequest precioEspecialRequest) {

        DistribuidorEntity distribuidor = distribuidorRepository.findById(distribuidorId)
                .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado"));

        ProductoEntity producto = productoRepository.findById(precioEspecialRequest.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Buscar si ya existe un precio especial
        Optional<DistribuidorPrecioEntity> optional =
                distribuidorPrecioRepository.findByDistribuidorIdAndProductoId(distribuidorId, precioEspecialRequest.getProductoId());

        DistribuidorPrecioEntity precioEspecial;

        if (optional.isPresent()) {
            precioEspecial = optional.get();
            precioEspecial.setPrecioEspecial(precioEspecialRequest.getPrecioEspecial());
        } else {
            precioEspecial = DistribuidorPrecioEntity.builder()
                    .distribuidor(distribuidor)
                    .producto(producto)
                    .precioEspecial(precioEspecialRequest.getPrecioEspecial())
                    .build();
        }

        return distribuidorPrecioRepository.save(precioEspecial);
    }

    @Transactional
    public void eliminarPrecioEspecial(Long distribuidorId, Long productoId) {
        distribuidorPrecioRepository.deleteByDistribuidorIdAndProductoId(distribuidorId, productoId);
    }

    public Double obtenerPrecioEfectivo(Long distribuidorId, Long productoId) {

        return distribuidorPrecioRepository
                .findByDistribuidorIdAndProductoId(distribuidorId, productoId)
                .map(DistribuidorPrecioEntity::getPrecioEspecial)
                .orElseGet(() -> {
                    ProductoEntity producto = productoRepository.findById(productoId)
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    return producto.getPrecioMayorista();
                });
    }
}
