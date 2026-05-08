package com.logistica.app.productos.controller;
import com.logistica.commons.controller.CommonController;
import com.logistica.app.productos.entity.Producto;
import com.logistica.app.productos.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ProductoController extends CommonController<Producto,ProductoService> {
    @GetMapping("/filtrar/{nombre}")
    public ResponseEntity<?> filtrar(@PathVariable String nombre){return ResponseEntity.ok(service.findByNombre(nombre));}
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Producto>> bajoStock(){return ResponseEntity.ok(service.findBajoStock());}
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id,@RequestParam int cantidad){
        return ResponseEntity.ok(service.actualizarStock(id,cantidad));}
}