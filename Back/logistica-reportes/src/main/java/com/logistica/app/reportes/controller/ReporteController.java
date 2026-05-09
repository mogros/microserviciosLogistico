package com.logistica.app.reportes.controller;

import com.logistica.app.reportes.clients.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    @Autowired private VentaClient   ventaClient;
    @Autowired private CompraClient  compraClient;
    @Autowired private ProductoClient productoClient;
    @Autowired private ClienteClient clienteClient;

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        List<Map<String,Object>> ventas   = safe(() -> ventaClient.listar());
        List<Map<String,Object>> compras  = safe(() -> compraClient.listar());
        List<Map<String,Object>> productos = safe(() -> productoClient.listar());
        List<Map<String,Object>> clientes  = safe(() -> clienteClient.listar());
        List<Map<String,Object>> bajoStock = safe(() -> productoClient.bajoStock());

        BigDecimal totalVentas  = sumar(ventas,  "total");
        BigDecimal totalCompras = sumar(compras, "total");

        Map<String,Object> res = new LinkedHashMap<>();
        res.put("totalVentas",        totalVentas);
        res.put("totalCompras",       totalCompras);
        res.put("utilidadBruta",      totalVentas.subtract(totalCompras));
        res.put("cantidadVentas",     ventas.size());
        res.put("cantidadCompras",    compras.size());
        res.put("ventasPagadas",      ventas.stream().filter(v -> "PAGADO".equals(v.get("estado"))).count());
        res.put("ventasCredito",      ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago"))).count());
        res.put("totalClientes",      clientes.size());
        res.put("clientesPremium",    clientes.stream().filter(c -> !"REGULAR".equals(c.get("tipo"))).count());
        res.put("totalProductos",     productos.size());
        res.put("productosBajoStock", bajoStock.size());
        res.put("letrasVencidas",     safe(() -> ventaClient.letrasVencidas()).size());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/ventas-por-mes")
    public ResponseEntity<?> ventasPorMes() {
        String inicio = LocalDate.now().withDayOfYear(1).format(DateTimeFormatter.ISO_DATE);
        String fin    = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        List<Map<String,Object>> ventas = safe(() -> ventaClient.findByPeriodo(inicio, fin));
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        Map<String,BigDecimal> porMes = new LinkedHashMap<>();
        for (String m : meses) porMes.put(m, BigDecimal.ZERO);
        ventas.forEach(v -> {
            String fecha = Objects.toString(v.get("fecha"), "");
            if (fecha.length() >= 7) {
                int mes = Integer.parseInt(fecha.substring(5,7)) - 1;
                porMes.merge(meses[mes], toBD(v.get("total")), BigDecimal::add);
            }
        });
        return ResponseEntity.ok(porMes.entrySet().stream()
            .map(e -> Map.of("mes",(Object)e.getKey(),"total",(Object)e.getValue()))
            .collect(Collectors.toList()));
    }

    @GetMapping("/ventas-vs-compras")
    public ResponseEntity<?> ventasVsCompras() {
        String inicio = LocalDate.now().withDayOfYear(1).format(DateTimeFormatter.ISO_DATE);
        String fin    = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        List<Map<String,Object>> ventas  = safe(() -> ventaClient.findByPeriodo(inicio, fin));
        List<Map<String,Object>> compras = safe(() -> compraClient.findByPeriodo(inicio, fin));
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        Map<String,BigDecimal[]> data = new LinkedHashMap<>();
        for (String m : meses) data.put(m, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        ventas.forEach(v -> acumularPorMes(data, meses, v, 0));
        compras.forEach(c -> acumularPorMes(data, meses, c, 1));
        List<Map<String,Object>> resultado = new ArrayList<>();
        data.forEach((mes, vals) -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("mes", mes); m.put("ventas", vals[0]); m.put("compras", vals[1]);
            m.put("utilidad", vals[0].subtract(vals[1]));
            resultado.add(m);
        });
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<?> topClientes() {
        List<Map<String,Object>> ventas   = safe(() -> ventaClient.listar());
        List<Map<String,Object>> clientes = safe(() -> clienteClient.listar());

        Map<String,BigDecimal> acumulado  = new HashMap<>();
        Map<String,String>     nombres    = new HashMap<>();

        for (Map<String,Object> v : ventas) {
            if ("ANULADO".equals(v.get("estado"))) continue;
            Object cidObj = v.get("clienteId");
            if (cidObj == null) continue;
            String cid = String.valueOf(cidObj);
            acumulado.merge(cid, toBD(v.get("total")), BigDecimal::add);
            String nombre = Objects.toString(v.get("clienteNombre"), "").trim();
            if (!nombre.isEmpty()) nombres.put(cid, nombre);
        }

        Map<String,Map<String,Object>> clientesMap = new HashMap<>();
        for (Map<String,Object> c : clientes) clientesMap.put(String.valueOf(c.get("id")), c);

        List<Map<String,Object>> resultado = acumulado.entrySet().stream()
            .sorted(Map.Entry.<String,BigDecimal>comparingByValue().reversed())
            .limit(10)
            .map(e -> {
                Map<String,Object> m = new LinkedHashMap<>();
                Map<String,Object> c = clientesMap.get(e.getKey());
                String nombre = nombres.getOrDefault(e.getKey(), "");
                if (nombre.isEmpty() && c != null)
                    nombre = Objects.toString(c.get("nombre"),"") + " " + Objects.toString(c.get("apellido"),"");
                m.put("nombre", nombre.trim().isEmpty() ? "Cliente #" + e.getKey() : nombre.trim());
                m.put("tipo",   c != null ? c.get("tipo") : "REGULAR");
                m.put("totalAcumulado", e.getValue());
                return m;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ventas-por-tipo")
    public ResponseEntity<?> ventasPorTipoPago() {
        List<Map<String,Object>> ventas = safe(() -> ventaClient.listar());
        long contado = ventas.stream().filter(v -> "CONTADO".equals(v.get("tipoPago"))).count();
        long credito = ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago"))).count();
        BigDecimal mContado = ventas.stream().filter(v -> "CONTADO".equals(v.get("tipoPago")))
            .map(v -> toBD(v.get("total"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mCredito = ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago")))
            .map(v -> toBD(v.get("total"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of(
            "contado", Map.of("cantidad", contado, "monto", mContado),
            "credito", Map.of("cantidad", credito, "monto", mCredito)
        ));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<?> bajoStock() {
        List<Map<String,Object>> productos = safe(() -> productoClient.bajoStock());
        log.info("Productos bajo stock recibidos via Feign: {}", productos.size());
        productos.forEach(p -> log.info("  - {} stock={} reorden={}", p.get("nombre"), p.get("stock"), p.get("puntoReorden")));
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/letras-vencidas")
    public ResponseEntity<?> letrasVencidas() {
        List<Map<String,Object>> letras = safe(() -> ventaClient.letrasVencidas());
        if (!letras.isEmpty()) {
            log.info("Primera letra vencida keys: {}", letras.get(0).keySet());
            log.info("Primera letra vencida valores: {}", letras.get(0));
        }
        return ResponseEntity.ok(letras);
    }

    // ── Helpers ──────────────────────────────────────────────────

    private BigDecimal toBD(Object val) {
        String s = Objects.toString(val, "0");
        try { return new BigDecimal(s == null || s.isBlank() ? "0" : s); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private BigDecimal sumar(List<Map<String,Object>> lista, String campo) {
        return lista.stream().map(m -> toBD(m.get(campo))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void acumularPorMes(Map<String,BigDecimal[]> data, String[] meses,
                                 Map<String,Object> item, int idx) {
        String fecha = Objects.toString(item.get("fecha"), "");
        if (fecha.length() >= 7) {
            try {
                int mes = Integer.parseInt(fecha.substring(5,7)) - 1;
                data.get(meses[mes])[idx] = data.get(meses[mes])[idx].add(toBD(item.get("total")));
            } catch (Exception ignored) {}
        }
    }

    /** Llama al supplier y devuelve lista vacía si falla (evita que un microservicio caído rompa todo) */
    @SuppressWarnings("unchecked")
    private <T> List<T> safe(java.util.function.Supplier<List<T>> supplier) {
        try {
            List<T> result = supplier.get();
            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Feign call failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
