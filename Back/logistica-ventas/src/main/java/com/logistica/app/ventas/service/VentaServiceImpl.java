package com.logistica.app.ventas.service;

import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.ventas.clients.ProductoClient;
import com.logistica.app.ventas.entity.*;
import com.logistica.app.ventas.repository.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VentaServiceImpl extends CommonServiceImpl<Venta, VentaRepository> implements VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);

    @Autowired private LetraVentaRepository   letraRepo;
    @Autowired private DetalleVentaRepository detalleRepo;
    @Autowired private ProductoClient         productoClient;

    private final AtomicLong counter = new AtomicLong(1);

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

        List<DetalleVenta> detalles = new ArrayList<>(venta.getDetalles());
        venta.getDetalles().clear();

        // ── Validar stock ANTES de guardar nada ────────────────────────
        for (DetalleVenta det : detalles) {
            try {
                Map<String, Object> producto = productoClient.obtenerProducto(det.getProductoId());
                Object stockObj = producto.get("stock");
                int stockDisponible = stockObj != null ? Integer.parseInt(stockObj.toString()) : 0;

                if (det.getCantidad() > stockDisponible) {
                    throw new RuntimeException(
                        "Stock insuficiente para '" + det.getNombreProducto() + "'. " +
                        "Disponible: " + stockDisponible + ", solicitado: " + det.getCantidad()
                    );
                }
                log.info("Stock OK: {} — disponible={} solicitado={}", det.getNombreProducto(), stockDisponible, det.getCantidad());
            } catch (RuntimeException e) {
                throw e; // re-lanzar errores de stock
            } catch (Exception e) {
                log.warn("No se pudo verificar stock del producto {}: {}", det.getProductoId(), e.getMessage());
                // Si no se puede verificar, continuar (mejor esfuerzo)
            }
        }

        // ── Número de venta ────────────────────────────────────────────
        venta.setNumeroVenta("V-" + String.format("%06d", counter.getAndIncrement()));

        // ── Calcular totales ───────────────────────────────────────────
        BigDecimal igv = venta.getSubtotal()
                .multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);
        venta.setIgv(igv);
        venta.setTotal(venta.getSubtotal().add(igv));
        venta.setSaldoPendiente(venta.getTotal());

        if (venta.getTipoPago() == Venta.TipoPago.CONTADO) {
            venta.setMontoPagado(venta.getTotal());
            venta.setSaldoPendiente(BigDecimal.ZERO);
            venta.setEstado(Venta.EstadoVenta.PAGADO);
        } else {
            venta.setEstado(Venta.EstadoVenta.PENDIENTE);
        }

        // ── Guardar venta sin detalles ─────────────────────────────────
        Venta saved = repository.save(venta);

        // ── Guardar detalles y reducir stock ───────────────────────────
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

            // Reducir stock (cantidad negativa = salida)
            try {
                productoClient.actualizarStock(det.getProductoId(), -det.getCantidad());
                log.info("Stock reducido: producto={} -{}", det.getProductoId(), det.getCantidad());
            } catch (Exception e) {
                log.warn("No se pudo reducir stock del producto {}: {}", det.getProductoId(), e.getMessage());
            }
        }
        saved.setDetalles(detallesGuardados);

        // ── Generar letras si es crédito ───────────────────────────────
        if (venta.getTipoPago() == Venta.TipoPago.CREDITO && cuotas > 0) {
            BigDecimal montoPorLetra = saved.getTotal()
                    .divide(new BigDecimal(cuotas), 2, RoundingMode.HALF_UP);
            List<LetraVenta> letras = new ArrayList<>();
            for (int i = 1; i <= cuotas; i++) {
                LetraVenta letra = new LetraVenta();
                letra.setVenta(saved);
                letra.setNumeroVenta(saved.getNumeroVenta());
                letra.setClienteNombre(saved.getClienteNombre());
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


    @Override
    @Transactional
    public Venta anularVenta(Long id) {
        Venta v = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + id));
        if (v.getEstado() == Venta.EstadoVenta.ANULADO) {
            throw new RuntimeException("La venta ya está anulada");
        }
        // Devolver el stock de cada producto
        List<DetalleVenta> detalles = detalleRepo.findByVentaId(id);
        for (DetalleVenta det : detalles) {
            try {
                productoClient.actualizarStock(det.getProductoId(), det.getCantidad());
                log.info("Stock devuelto al anular: producto={} +{}", det.getProductoId(), det.getCantidad());
            } catch (Exception e) {
                log.warn("No se pudo devolver stock del producto {}: {}", det.getProductoId(), e.getMessage());
            }
        }
        v.setEstado(Venta.EstadoVenta.ANULADO);
        return repository.save(v);
    }

    @Override public List<Venta> findByCliente(Long id) { return repository.findByClienteId(id); }
    @Override public List<LetraVenta> findLetrasPorVenta(Long id) { return letraRepo.findByVentaId(id); }
    @Override public List<LetraVenta> findLetrasVencidas() {
        // Buscar letras ya marcadas como VENCIDO por el scheduler
        return letraRepo.findByEstado(LetraVenta.EstadoLetra.VENCIDO);
    }
    @Override public List<Venta> findByPeriodo(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin);
    }
}
