package com.logistica.app.ventas.service;

import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.ventas.entity.*;
import com.logistica.app.ventas.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VentaServiceImpl extends CommonServiceImpl<Venta, VentaRepository> implements VentaService {

    @Autowired private LetraVentaRepository letraRepo;
    @Autowired private DetalleVentaRepository detalleRepo;

    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Al arrancar, leer el mayor número de venta existente en la BD
     * para que el contador no genere duplicados al reiniciar.
     */
    @PostConstruct
    public void initCounter() {
        repository.findAll().stream()
            .map(Venta::getNumeroVenta)
            .filter(n -> n != null && n.startsWith("V-"))
            .mapToLong(n -> {
                try { return Long.parseLong(n.substring(2)); }
                catch (NumberFormatException e) { return 0L; }
            })
            .max()
            .ifPresent(max -> counter.set(max + 1));
    }

    @Override
    @Transactional
    public Venta crearVenta(Venta venta, int cuotas) {

        // 1. Separar detalles antes de guardar la venta
        List<DetalleVenta> detalles = new ArrayList<>(venta.getDetalles());
        venta.getDetalles().clear();

        // 2. Número de venta único
        venta.setNumeroVenta("V-" + String.format("%06d", counter.getAndIncrement()));

        // 3. Calcular totales
        BigDecimal igv = venta.getSubtotal()
                .multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);
        venta.setIgv(igv);
        venta.setTotal(venta.getSubtotal().add(igv));
        venta.setSaldoPendiente(venta.getTotal());

        // 4. Contado → pagado inmediatamente
        if (venta.getTipoPago() == Venta.TipoPago.CONTADO) {
            venta.setMontoPagado(venta.getTotal());
            venta.setSaldoPendiente(BigDecimal.ZERO);
            venta.setEstado(Venta.EstadoVenta.PAGADO);
        } else {
            venta.setEstado(Venta.EstadoVenta.PENDIENTE);
        }

        // 5. Guardar la venta SIN detalles para obtener el ID
        Venta saved = repository.save(venta);

        // 6. Guardar detalles con la referencia correcta
        List<DetalleVenta> detallesGuardados = new ArrayList<>();
        for (DetalleVenta det : detalles) {
            det.setId(null);
            det.setVenta(saved);
            det.setSubtotal(
                det.getPrecioUnitario()
                    .multiply(new BigDecimal(det.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP)
            );
            detallesGuardados.add(detalleRepo.save(det));
        }
        saved.setDetalles(detallesGuardados);

        // 7. Generar letras si es crédito
        if (venta.getTipoPago() == Venta.TipoPago.CREDITO && cuotas > 0) {
            BigDecimal montoPorLetra = saved.getTotal()
                    .divide(new BigDecimal(cuotas), 2, RoundingMode.HALF_UP);
            List<LetraVenta> letras = new ArrayList<>();
            for (int i = 1; i <= cuotas; i++) {
                LetraVenta letra = new LetraVenta();
                letra.setVenta(saved);
                letra.setNumeroLetra(i);
                letra.setMonto(montoPorLetra);
                letra.setFechaVencimiento(LocalDate.now().plusMonths(i));
                letra.setEstado(LetraVenta.EstadoLetra.PENDIENTE);
                letras.add(letraRepo.save(letra));
            }
            saved.setLetras(letras);
        }

        return saved;
    }

    @Override
    @Transactional
    public Venta registrarPago(Long ventaId, BigDecimal monto) {
        Venta v = repository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));
        v.setMontoPagado(v.getMontoPagado().add(monto));
        v.setSaldoPendiente(v.getTotal().subtract(v.getMontoPagado()));
        if (v.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            v.setEstado(Venta.EstadoVenta.PAGADO);
            v.setSaldoPendiente(BigDecimal.ZERO);
        } else {
            v.setEstado(Venta.EstadoVenta.PARCIAL);
        }
        return repository.save(v);
    }

    @Override
    @Transactional
    public LetraVenta pagarLetra(Long letraId) {
        LetraVenta letra = letraRepo.findById(letraId)
                .orElseThrow(() -> new RuntimeException("Letra no encontrada: " + letraId));
        letra.setMontoPagado(letra.getMonto());
        letra.setEstado(LetraVenta.EstadoLetra.PAGADO);
        letra.setFechaPago(LocalDate.now());
        letraRepo.save(letra);
        registrarPago(letra.getVenta().getId(), letra.getMonto());
        return letra;
    }

    @Override public List<Venta> findByCliente(Long id) { return repository.findByClienteId(id); }
    @Override public List<LetraVenta> findLetrasPorVenta(Long id) { return letraRepo.findByVentaId(id); }
    @Override public List<LetraVenta> findLetrasVencidas() {
        return letraRepo.findByFechaVencimientoBeforeAndEstado(
            LocalDate.now(), LetraVenta.EstadoLetra.PENDIENTE);
    }
    @Override public List<Venta> findByPeriodo(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}
