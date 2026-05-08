package com.logistica.app.compras.service;

import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.compras.entity.*;
import com.logistica.app.compras.repository.*;
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
public class CompraServiceImpl extends CommonServiceImpl<Compra, CompraRepository> implements CompraService {

    @Autowired private LetraCompraRepository letraRepo;
    @Autowired private DetalleCompraRepository detalleRepo;

    private final AtomicLong counter = new AtomicLong(1);

    @PostConstruct
    public void initCounter() {
        repository.findAll().stream()
            .map(Compra::getNumeroCompra)
            .filter(n -> n != null && n.startsWith("C-"))
            .mapToLong(n -> {
                try { return Long.parseLong(n.substring(2)); }
                catch (NumberFormatException e) { return 0L; }
            })
            .max()
            .ifPresent(max -> counter.set(max + 1));
    }

    @Override
    @Transactional
    public Compra crearCompra(Compra compra, int cuotas) {

        List<DetalleCompra> detalles = new ArrayList<>(compra.getDetalles());
        compra.getDetalles().clear();

        compra.setNumeroCompra("C-" + String.format("%06d", counter.getAndIncrement()));

        BigDecimal igv = compra.getSubtotal()
                .multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);
        compra.setIgv(igv);
        compra.setTotal(compra.getSubtotal().add(igv));
        compra.setSaldoPendiente(compra.getTotal());

        if (compra.getTipoPago() == Compra.TipoPago.CONTADO) {
            compra.setMontoPagado(compra.getTotal());
            compra.setSaldoPendiente(BigDecimal.ZERO);
            compra.setEstado(Compra.EstadoCompra.PAGADO);
        } else {
            compra.setEstado(Compra.EstadoCompra.PENDIENTE);
        }

        Compra saved = repository.save(compra);

        List<DetalleCompra> detallesGuardados = new ArrayList<>();
        for (DetalleCompra det : detalles) {
            det.setId(null);
            det.setCompra(saved);
            det.setSubtotal(
                det.getPrecioUnitario()
                    .multiply(new BigDecimal(det.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP)
            );
            detallesGuardados.add(detalleRepo.save(det));
        }
        saved.setDetalles(detallesGuardados);

        if (compra.getTipoPago() == Compra.TipoPago.CREDITO && cuotas > 0) {
            BigDecimal montoPorLetra = saved.getTotal()
                    .divide(new BigDecimal(cuotas), 2, RoundingMode.HALF_UP);
            List<LetraCompra> letras = new ArrayList<>();
            for (int i = 1; i <= cuotas; i++) {
                LetraCompra letra = new LetraCompra();
                letra.setCompra(saved);
                letra.setNumeroLetra(i);
                letra.setMonto(montoPorLetra);
                letra.setFechaVencimiento(LocalDate.now().plusMonths(i));
                letra.setEstado(LetraCompra.EstadoLetra.PENDIENTE);
                letras.add(letraRepo.save(letra));
            }
            saved.setLetras(letras);
        }

        return saved;
    }

    @Override
    @Transactional
    public Compra registrarPago(Long compraId, BigDecimal monto) {
        Compra c = repository.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada: " + compraId));
        c.setMontoPagado(c.getMontoPagado().add(monto));
        c.setSaldoPendiente(c.getTotal().subtract(c.getMontoPagado()));
        if (c.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            c.setEstado(Compra.EstadoCompra.PAGADO);
            c.setSaldoPendiente(BigDecimal.ZERO);
        } else {
            c.setEstado(Compra.EstadoCompra.PARCIAL);
        }
        return repository.save(c);
    }

    @Override
    @Transactional
    public LetraCompra pagarLetra(Long letraId) {
        LetraCompra l = letraRepo.findById(letraId)
                .orElseThrow(() -> new RuntimeException("Letra no encontrada: " + letraId));
        l.setMontoPagado(l.getMonto());
        l.setEstado(LetraCompra.EstadoLetra.PAGADO);
        l.setFechaPago(LocalDate.now());
        letraRepo.save(l);
        registrarPago(l.getCompra().getId(), l.getMonto());
        return l;
    }

    @Override public List<Compra> findByProveedor(Long id) { return repository.findByProveedorId(id); }
    @Override public List<LetraCompra> findLetrasPorCompra(Long id) { return letraRepo.findByCompraId(id); }
    @Override public List<LetraCompra> findLetrasVencidas() {
        return letraRepo.findByFechaVencimientoBeforeAndEstado(
            LocalDate.now(), LetraCompra.EstadoLetra.PENDIENTE);
    }
    @Override public List<Compra> findByPeriodo(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}
