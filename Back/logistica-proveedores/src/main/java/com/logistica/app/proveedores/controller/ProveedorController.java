package com.logistica.app.proveedores.controller;
import com.logistica.commons.controller.CommonController;
import com.logistica.app.proveedores.entity.Proveedor;
import com.logistica.app.proveedores.service.ProveedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
public class ProveedorController extends CommonController<Proveedor,ProveedorService> {
    @GetMapping("/buscar/{texto}")
    public ResponseEntity<?> buscar(@PathVariable String texto){return ResponseEntity.ok(service.buscar(texto));}
}