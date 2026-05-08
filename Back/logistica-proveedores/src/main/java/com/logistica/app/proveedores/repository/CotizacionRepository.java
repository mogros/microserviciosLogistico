package com.logistica.app.proveedores.repository;
import com.logistica.app.proveedores.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CotizacionRepository extends JpaRepository<Cotizacion,Long> {
    List<Cotizacion> findByProveedorId(Long proveedorId);
    List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado);
}