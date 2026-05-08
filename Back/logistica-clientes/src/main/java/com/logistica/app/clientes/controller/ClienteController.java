package com.logistica.app.clientes.controller;
import com.logistica.commons.controller.CommonController;
import com.logistica.app.clientes.entity.Cliente;
import com.logistica.app.clientes.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
public class ClienteController extends CommonController<Cliente,ClienteService> {
    @GetMapping("/buscar/{texto}")
    public ResponseEntity<?> buscar(@PathVariable String texto){return ResponseEntity.ok(service.buscar(texto));}
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> porTipo(@PathVariable String tipo){
        return ResponseEntity.ok(service.findByTipo(Cliente.TipoCliente.valueOf(tipo.toUpperCase())));}
    @PutMapping("/{id}/acumulado")
    public ResponseEntity<?> actualizarAcumulado(@PathVariable Long id,@RequestParam BigDecimal monto){
        return ResponseEntity.ok(service.actualizarAcumulado(id,monto));}
}