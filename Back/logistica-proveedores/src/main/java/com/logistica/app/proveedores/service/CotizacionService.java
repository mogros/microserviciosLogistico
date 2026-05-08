package com.logistica.app.proveedores.service;
import com.logistica.commons.service.CommonService;
import com.logistica.app.proveedores.entity.Cotizacion;
import java.util.List;
public interface CotizacionService extends CommonService<Cotizacion> {
    List<Cotizacion> findByProveedor(Long proveedorId);
    List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado);
    Cotizacion aprobar(Long id);
    Cotizacion rechazar(Long id);
}