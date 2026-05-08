package com.logistica.app.ventas.service;

import com.logistica.commons.service.CommonService;
import com.logistica.app.ventas.entity.Venta;
import com.logistica.app.ventas.entity.LetraVenta;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaService extends CommonService<Venta> {
    Venta crearVenta(Venta venta, int cuotas);
    Venta registrarPago(Long ventaId, BigDecimal monto);
    LetraVenta pagarLetra(Long letraId);
    Venta anularVenta(Long id);
    List<Venta> findByCliente(Long clienteId);
    List<LetraVenta> findLetrasPorVenta(Long ventaId);
    List<LetraVenta> findLetrasVencidas();
    List<Venta> findByPeriodo(LocalDate inicio, LocalDate fin);
}
