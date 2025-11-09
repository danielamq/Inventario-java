package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.VentaRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.DistribuidorEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.ProductoEntity;
import dharmaInventario.dharmaInventario.domain.model.Entity.VentaEntity;
import dharmaInventario.dharmaInventario.domain.Repository.DistribuidoresRepository;
import dharmaInventario.dharmaInventario.domain.Repository.ProductoRepository;
import dharmaInventario.dharmaInventario.domain.Repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final DistribuidoresRepository distribuidorRepository;
    private final DistribuidoresRepository distribuidoresRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, DistribuidoresRepository distribuidoresRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.distribuidoresRepository = distribuidoresRepository;
        this.distribuidorRepository = distribuidoresRepository;
    }
    public List<VentaEntity> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public ProductoEntity crearVenta(VentaRequest request) {
        if (request.getNombreCliente() == null) {
            throw new IllegalArgumentException("La venta debe tener un cliente");
        }

        List<ProductoEntity> productos = productoRepository.findByNombreIgnoreCaseContaining(request.getNombreProducto());
        if (productos.isEmpty()) {
            throw new RuntimeException("No se encontró ningún producto parecido a: " + request.getNombreProducto());
        }

        ProductoEntity productoVendido = productos.get(0);

        VentaEntity venta = new VentaEntity();
        venta.setNombreCliente(request.getNombreCliente());
        venta.setCantidad(request.getCantidad());
        venta.setFecha(new Date());
        venta.setProducto(productoVendido);
        venta.setEsMayorista(request.isEsMayorista());

        // Si tiene distribuidor, lo buscamos
        if (request.getDistribuidorId() != null) {
            DistribuidorEntity distribuidor = distribuidorRepository.findById(request.getDistribuidorId())
                    .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado con ID: " + request.getDistribuidorId()));

            venta.setDistribuidor(distribuidor);

            // 🔹 Descontar del stock consignado

            if (distribuidor.getCantidadConsignada() < request.getCantidad()) {
                throw new RuntimeException("El distribuidor no tiene suficiente stock en consignación.");
            }

            distribuidor.setCantidadConsignada(distribuidor.getCantidadConsignada() - request.getCantidad());
            productoVendido.setCantidadConsignacion(productoVendido.getCantidadConsignacion() - request.getCantidad());

            distribuidorRepository.save(distribuidor);
        } else {
            // 🔹 Descontar del stock real
            int nuevoStock = productoVendido.getCantidadReal() - request.getCantidad();
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para este producto.");
            }
            productoVendido.setCantidadReal(nuevoStock);
        }

        // Establecer precio según tipo de venta
        if (request.isEsMayorista()) {
            venta.setPrecioUsado(productoVendido.getPrecioMayorista());
        } else {
            venta.setPrecioUsado(productoVendido.getPrecioDetal());
        }

        productoRepository.save(productoVendido);
        ventaRepository.save(venta);

        return productoVendido;
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

}
