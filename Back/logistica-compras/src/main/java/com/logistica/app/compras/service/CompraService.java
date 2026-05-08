package com.logistica.app.compras.service;
import com.logistica.commons.service.CommonService;
import com.logistica.app.compras.entity.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public interface CompraService extends CommonService<Compra> {
    Compra crearCompra(Compra compra, int cuotas);
    Compra registrarPago(Long compraId, BigDecimal monto);
    LetraCompra pagarLetra(Long letraId);
    List<Compra> findByProveedor(Long proveedorId);
    List<LetraCompra> findLetrasPorCompra(Long compraId);
    List<LetraCompra> findLetrasVencidas();
    List<Compra> findByPeriodo(LocalDate inicio, LocalDate fin);
}