package com.logistica.app.proveedores.controller;
import com.logistica.commons.controller.CommonController;
import com.logistica.app.proveedores.entity.Cotizacion;
import com.logistica.app.proveedores.service.CotizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/cotizaciones")
public class CotizacionController extends CommonController<Cotizacion,CotizacionService> {
    @GetMapping("/proveedor/{id}")
    public ResponseEntity<?> porProveedor(@PathVariable Long id){return ResponseEntity.ok(service.findByProveedor(id));}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> porEstado(@PathVariable String estado){
        return ResponseEntity.ok(service.findByEstado(Cotizacion.EstadoCotizacion.valueOf(estado.toUpperCase())));}
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Long id){return ResponseEntity.ok(service.aprobar(id));}
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id){return ResponseEntity.ok(service.rechazar(id));}
}