package com.logistica.app.proveedores.service;
import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.proveedores.entity.Cotizacion;
import com.logistica.app.proveedores.repository.CotizacionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CotizacionServiceImpl extends CommonServiceImpl<Cotizacion,CotizacionRepository> implements CotizacionService {
    public List<Cotizacion> findByProveedor(Long id){return repository.findByProveedorId(id);}
    public List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion e){return repository.findByEstado(e);}
    public Cotizacion aprobar(Long id){
        Cotizacion c = repository.findById(id).orElseThrow(()->new RuntimeException("Cotizacion no encontrada"));
        c.setEstado(Cotizacion.EstadoCotizacion.APROBADA);
        return repository.save(c);
    }
    public Cotizacion rechazar(Long id){
        Cotizacion c = repository.findById(id).orElseThrow(()->new RuntimeException("Cotizacion no encontrada"));
        c.setEstado(Cotizacion.EstadoCotizacion.RECHAZADA);
        return repository.save(c);
    }
}