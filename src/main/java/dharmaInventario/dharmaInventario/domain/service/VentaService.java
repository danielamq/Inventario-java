package dharmaInventario.dharmaInventario.domain.service;

import dharmaInventario.dharmaInventario.domain.model.DTO.VentaProductoRequest;
import dharmaInventario.dharmaInventario.domain.model.DTO.VentaRequest;
import dharmaInventario.dharmaInventario.domain.model.Entity.*;
import dharmaInventario.dharmaInventario.domain.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final DistribuidoresRepository distribuidoresRepository;
    private final DistribuidorPrecioRepository distribuidorPrecioRepository;
    private final DistribuidorProductoRepository distribuidorProductoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, DistribuidoresRepository distribuidoresRepository, DistribuidorPrecioRepository distribuidorPrecioRepository, DistribuidorProductoRepository distribuidorProductoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.distribuidoresRepository = distribuidoresRepository;
        this.distribuidorPrecioRepository = distribuidorPrecioRepository;
        this.distribuidorProductoRepository = distribuidorProductoRepository;
    }

    @Transactional
    public VentaEntity crearVenta(VentaRequest ventaRequest) {

        if (ventaRequest.getNombreCliente() == null || ventaRequest.getProductos() == null || ventaRequest.getProductos().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener cliente y al menos un producto");
        }

        VentaEntity venta = new VentaEntity();
        venta.setNombreCliente(ventaRequest.getNombreCliente());
        venta.setEsMayorista(ventaRequest.isEsMayorista());
        venta.setDescuentoAdicional(ventaRequest.getDescuentoAdicional());
        venta.setFecha(new Date());

        DistribuidorEntity distribuidor = null;
        if (ventaRequest.getDistribuidorId() != null) {
            distribuidor = distribuidoresRepository.findById(ventaRequest.getDistribuidorId())
                    .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado"));
            venta.setDistribuidor(distribuidor);
        }

        List<VentaItemEntity> items = new ArrayList<>();
        double totalVenta = 0.0;

        for (VentaProductoRequest productoInVenta : ventaRequest.getProductos()) {

            ProductoEntity producto = productoRepository.findById(productoInVenta.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado id: " + productoInVenta.getProductoId()));


            Optional<DistribuidorPrecioEntity> precioEspecial =
                    distribuidorPrecioRepository.findByDistribuidorIdAndProductoId(
                            ventaRequest.getDistribuidorId(),
                            producto.getId()
                    );
            double precioUsado;
            if (precioEspecial.isPresent()) {
                precioUsado = precioEspecial.get().getPrecioEspecial();
            } else if (ventaRequest.isEsMayorista()) {
                precioUsado = producto.getPrecioMayorista();
            } else {
                precioUsado = producto.getPrecioDetal();
            }

            // Aplicar descuento adicional si existe
            if (ventaRequest.getDescuentoAdicional() != null && ventaRequest.getDescuentoAdicional() > 0) {
                precioUsado -= ventaRequest.getDescuentoAdicional();
            }

            // Crear item
            VentaItemEntity item = new VentaItemEntity();
            item.setVenta(venta);
            item.setProducto(producto);
            item.setCantidad(productoInVenta.getCantidad());
            item.setPrecioUnitario(precioUsado);
            item.setSubtotal(precioUsado  * productoInVenta.getCantidad());

            items.add(item);
            totalVenta += item.getSubtotal();

            if (distribuidor != null) { // Venta por distribuidor → stock consignado

                // Buscar la consignación específica del distribuidor para este producto
                DistribuidorProductoEntity distribuidorProducto =
                        distribuidorProductoRepository.findByDistribuidorIdAndProductoId(
                                distribuidor.getId(),
                                producto.getId()
                        ).orElseThrow(() -> new RuntimeException("El distribuidor no tiene consignación de este producto"));

                if (distribuidorProducto.getCantidadConsignada() < productoInVenta.getCantidad()) {
                    throw new RuntimeException("Stock consignado insuficiente");
                }

                distribuidorProducto.setCantidadConsignada(distribuidorProducto.getCantidadConsignada() - productoInVenta.getCantidad());
                distribuidorProductoRepository.save(distribuidorProducto);

                producto.setCantidadConsignacion(producto.getCantidadConsignacion() - productoInVenta.getCantidad());

            } else {
                if (producto.getCantidadReal() < productoInVenta.getCantidad()) {
                    throw new RuntimeException("Stock real insuficiente");
                }
                producto.setCantidadReal(producto.getCantidadReal() - productoInVenta.getCantidad());
            }

            productoRepository.save(producto);
        }

        venta.setTotal(totalVenta);
        venta.setItems(items);

        return ventaRepository.save(venta);
    }

    public VentaEntity obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public List<VentaEntity> listarVentas() {
        return ventaRepository.findAll();
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }
}

