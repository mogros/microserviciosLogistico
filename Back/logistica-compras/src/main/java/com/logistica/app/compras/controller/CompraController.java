package com.logistica.app.compras.controller;

import com.logistica.commons.controller.CommonController;
import com.logistica.app.compras.dto.CompraRequest;
import com.logistica.app.compras.entity.Compra;
import com.logistica.app.compras.entity.DetalleCompra;
import com.logistica.app.compras.service.CompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CompraController extends CommonController<Compra, CompraService> {

    @PostMapping("/nueva")
    public ResponseEntity<?> crear(@RequestBody CompraRequest req) {
        try {
            Compra compra = new Compra();
            compra.setProveedorId(req.getProveedorId());
            compra.setProveedorNombre(req.getProveedorNombre());
            compra.setSubtotal(req.getSubtotal() != null ? req.getSubtotal() : BigDecimal.ZERO);
            compra.setObservaciones(req.getObservaciones());
            compra.setFecha(LocalDate.now());

            try {
                compra.setTipoPago(Compra.TipoPago.valueOf(
                    req.getTipoPago() != null ? req.getTipoPago().toUpperCase() : "CONTADO"
                ));
            } catch (IllegalArgumentException e) {
                compra.setTipoPago(Compra.TipoPago.CONTADO);
            }

            List<DetalleCompra> detalles = new ArrayList<>();
            if (req.getDetalles() != null) {
                for (CompraRequest.DetalleRequest dr : req.getDetalles()) {
                    DetalleCompra det = new DetalleCompra();
                    det.setProductoId(dr.getProductoId());
                    det.setNombreProducto(dr.getNombreProducto());
                    det.setCantidad(dr.getCantidad());
                    det.setPrecioUnitario(dr.getPrecioUnitario());
                    det.setSubtotal(dr.getPrecioUnitario()
                        .multiply(new BigDecimal(dr.getCantidad())));
                    detalles.add(det);
                }
            }
            compra.setDetalles(detalles);

            if (!detalles.isEmpty()) {
                BigDecimal subtotal = detalles.stream()
                    .map(d -> d.getPrecioUnitario().multiply(new BigDecimal(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                compra.setSubtotal(subtotal);
            }

            Compra resultado = service.crearCompra(compra, req.getCuotas());
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}/pago")
    public ResponseEntity<?> registrarPago(@PathVariable Long id,
                                           @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(service.registrarPago(id, monto));
    }

    @PutMapping("/letras/{letraId}/pagar")
    public ResponseEntity<?> pagarLetra(@PathVariable Long letraId) {
        return ResponseEntity.ok(service.pagarLetra(letraId));
    }

    @GetMapping("/proveedor/{id}")
    public ResponseEntity<?> porProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByProveedor(id));
    }

    @GetMapping("/{id}/letras")
    public ResponseEntity<?> letras(@PathVariable Long id) {
        return ResponseEntity.ok(service.findLetrasPorCompra(id));
    }

    @GetMapping("/letras/vencidas")
    public ResponseEntity<?> letrasVencidas() {
        return ResponseEntity.ok(service.findLetrasVencidas());
    }

    @GetMapping("/periodo")
    public ResponseEntity<?> porPeriodo(@RequestParam String inicio,
                                        @RequestParam String fin) {
        return ResponseEntity.ok(
            service.findByPeriodo(LocalDate.parse(inicio), LocalDate.parse(fin)));
    }
}
