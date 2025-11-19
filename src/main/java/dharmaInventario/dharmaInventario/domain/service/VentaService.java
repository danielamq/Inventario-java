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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final DistribuidoresRepository distribuidorRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, DistribuidoresRepository distribuidoresRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.distribuidorRepository = distribuidoresRepository;
    }
    public List<VentaEntity> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public List<VentaEntity> procesarVentas(List<VentaRequest> ventas) {

        List<VentaEntity> resultado = new ArrayList<>();

        for (VentaRequest req : ventas) {
            resultado.add(procesarVentaIndividual(req));
        }

        return resultado;
    }

    @Transactional
    public VentaEntity procesarVentaIndividual(VentaRequest request) {

        if (request.getNombreCliente() == null) {
            throw new IllegalArgumentException("La venta debe tener un cliente");
        }

        ProductoEntity producto = productoRepository.findById((long) request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        VentaEntity venta = new VentaEntity();
        venta.setNombreCliente(request.getNombreCliente());
        venta.setCantidad(request.getCantidad());
        venta.setFecha(new Date());
        venta.setProducto(producto);
        venta.setEsMayorista(request.isEsMayorista());
        venta.setDescuentoAdicional(request.getDescuentoAdicional());

        // 🔹 Si tiene distribuidor — descontar consignación
        if (request.getDistribuidorId() != null) {

            DistribuidorEntity distribuidor = distribuidorRepository.findById(request.getDistribuidorId())
                    .orElseThrow(() -> new RuntimeException("Distribuidor no encontrado"));

            if (distribuidor.getCantidadConsignada() < request.getCantidad()) {
                throw new RuntimeException("Stock consignado insuficiente.");
            }

            distribuidor.setCantidadConsignada(
                    distribuidor.getCantidadConsignada() - request.getCantidad()
            );

            producto.setCantidadConsignacion(
                    producto.getCantidadConsignacion() - request.getCantidad()
            );

            venta.setDistribuidor(distribuidor);

            distribuidorRepository.save(distribuidor);

        } else {
            // 🔹 Si no tiene distribuidor — descontar stock real
            int nuevoStock = producto.getCantidadReal() - request.getCantidad();
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente del producto.");
            }
            producto.setCantidadReal(nuevoStock);
        }

        // 🔹 Determinar precio usado
        if (venta.isEsMayorista()) {
            venta.setPrecioUsado(producto.getPrecioMayorista());
            if (venta.getDescuentoAdicional() != null) {
                venta.setPrecioUsado(venta.getPrecioUsado() - venta.getDescuentoAdicional());
            }
        } else {
            venta.setPrecioUsado(producto.getPrecioDetal());
        }

        productoRepository.save(producto);
        return ventaRepository.save(venta);
    }

    public VentaEntity obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

}
