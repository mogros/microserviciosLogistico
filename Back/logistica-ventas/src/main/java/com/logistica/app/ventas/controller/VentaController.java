package com.logistica.app.ventas.controller;

import com.logistica.commons.controller.CommonController;
import com.logistica.app.ventas.dto.VentaRequest;
import com.logistica.app.ventas.entity.DetalleVenta;
import com.logistica.app.ventas.entity.Venta;
import com.logistica.app.ventas.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VentaController extends CommonController<Venta, VentaService> {

    /**
     * Crear venta con detalles.
     * Recibe un VentaRequest (DTO plano) para evitar problemas de
     * deserializacion con referencias circulares entre Venta y DetalleVenta.
     */
    @PostMapping("/nueva")
    public ResponseEntity<?> crear(@RequestBody VentaRequest req) {
        try {
            // Construir entidad Venta desde el DTO
            Venta venta = new Venta();
            venta.setClienteId(req.getClienteId());
            venta.setClienteNombre(req.getClienteNombre());
            venta.setSubtotal(req.getSubtotal() != null ? req.getSubtotal() : BigDecimal.ZERO);
            venta.setObservaciones(req.getObservaciones());
            venta.setFecha(LocalDate.now());

            // Tipo de pago
            try {
                venta.setTipoPago(Venta.TipoPago.valueOf(
                    req.getTipoPago() != null ? req.getTipoPago().toUpperCase() : "CONTADO"
                ));
            } catch (IllegalArgumentException e) {
                venta.setTipoPago(Venta.TipoPago.CONTADO);
            }

            // Construir detalles desde el DTO (sin referencia circular)
            List<DetalleVenta> detalles = new ArrayList<>();
            if (req.getDetalles() != null) {
                for (VentaRequest.DetalleRequest dr : req.getDetalles()) {
                    DetalleVenta det = new DetalleVenta();
                    det.setProductoId(dr.getProductoId());
                    det.setNombreProducto(dr.getNombreProducto());
                    det.setCantidad(dr.getCantidad());
                    det.setPrecioUnitario(dr.getPrecioUnitario());
                    // El subtotal se recalcula en el service
                    det.setSubtotal(dr.getPrecioUnitario()
                        .multiply(new BigDecimal(dr.getCantidad())));
                    detalles.add(det);
                }
            }
            venta.setDetalles(detalles);

            // Recalcular subtotal desde los detalles para mayor precisión
            if (!detalles.isEmpty()) {
                BigDecimal subtotal = detalles.stream()
                    .map(d -> d.getPrecioUnitario().multiply(new BigDecimal(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                venta.setSubtotal(subtotal);
            }

            Venta resultado = service.crearVenta(venta, req.getCuotas());
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

    @GetMapping("/cliente/{id}")
    public ResponseEntity<?> porCliente(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByCliente(id));
    }

    @GetMapping("/{id}/letras")
    public ResponseEntity<?> letras(@PathVariable Long id) {
        return ResponseEntity.ok(service.findLetrasPorVenta(id));
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
