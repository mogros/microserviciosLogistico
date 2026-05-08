package com.logistica.app.reportes.controller;

import com.logistica.app.reportes.clients.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Objects;
import java.util.stream.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired private VentaClient ventaClient;
    @Autowired private CompraClient compraClient;
    @Autowired private ProductoClient productoClient;
    @Autowired private ClienteClient clienteClient;

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        List<Map<String,Object>> ventas  = ventaClient.listar();
        List<Map<String,Object>> compras = compraClient.listar();
        List<Map<String,Object>> productos = productoClient.listar();
        List<Map<String,Object>> clientes  = clienteClient.listar();

        BigDecimal totalVentas = sumar(ventas, "total");
        BigDecimal totalCompras = sumar(compras, "total");

        long ventasPagadas = ventas.stream().filter(v -> "PAGADO".equals(v.get("estado"))).count();
        long ventasCredito = ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago"))).count();
        long clientesPremium = clientes.stream().filter(c -> !"REGULAR".equals(c.get("tipo"))).count();
        long bajoStock = productoClient.bajoStock().size();

        Map<String,Object> res = new LinkedHashMap<>();
        res.put("totalVentas",            totalVentas);
        res.put("totalCompras",           totalCompras);
        res.put("utilidadBruta",          totalVentas.subtract(totalCompras));
        res.put("cantidadVentas",         ventas.size());
        res.put("cantidadCompras",        compras.size());
        res.put("ventasPagadas",          ventasPagadas);
        res.put("ventasCredito",          ventasCredito);
        res.put("totalClientes",          clientes.size());
        res.put("clientesPremium",        clientesPremium);
        res.put("totalProductos",         productos.size());
        res.put("productosBajoStock",     bajoStock);
        res.put("letrasVencidas",         ventaClient.letrasVencidas().size());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/ventas-por-mes")
    public ResponseEntity<?> ventasPorMes() {
        String inicio = LocalDate.now().withDayOfYear(1).format(DateTimeFormatter.ISO_DATE);
        String fin    = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        List<Map<String,Object>> ventas = ventaClient.findByPeriodo(inicio, fin);
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        Map<String,BigDecimal> porMes = new LinkedHashMap<>();
        for (String m : meses) porMes.put(m, BigDecimal.ZERO);
        ventas.forEach(v -> {
            String fecha = v.getOrDefault("fecha","").toString();
            if (fecha.length() >= 7) {
                int mes = Integer.parseInt(fecha.substring(5,7)) - 1;
                porMes.merge(meses[mes],
                    new BigDecimal(v.getOrDefault("total","0").toString()), BigDecimal::add);
            }
        });
        List<Map<String,Object>> resultado = porMes.entrySet().stream()
            .map(e -> Map.of("mes",(Object)e.getKey(),"total",(Object)e.getValue()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ventas-vs-compras")
    public ResponseEntity<?> ventasVsCompras() {
        String inicio = LocalDate.now().withDayOfYear(1).format(DateTimeFormatter.ISO_DATE);
        String fin    = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        List<Map<String,Object>> ventas  = ventaClient.findByPeriodo(inicio, fin);
        List<Map<String,Object>> compras = compraClient.findByPeriodo(inicio, fin);
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        Map<String,BigDecimal[]> data = new LinkedHashMap<>();
        for (String m : meses) data.put(m, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        ventas.forEach(v -> acumularPorMes(data, meses, v, 0));
        compras.forEach(c -> acumularPorMes(data, meses, c, 1));
        List<Map<String,Object>> resultado = new ArrayList<>();
        data.forEach((mes, vals) -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("mes", mes);
            m.put("ventas",   vals[0]);
            m.put("compras",  vals[1]);
            m.put("utilidad", vals[0].subtract(vals[1]));
            resultado.add(m);
        });
        return ResponseEntity.ok(resultado);
    }

    /**
     * Top clientes calculado desde las ventas reales.
     * No depende de totalComprasAcumuladas (que solo se actualiza
     * al registrar ventas via Feign, aún no implementado).
     */
    @GetMapping("/top-clientes")
    public ResponseEntity<?> topClientes() {
        List<Map<String,Object>> ventas   = ventaClient.listar();
        List<Map<String,Object>> clientes = clienteClient.listar();

        // Agrupar ventas por clienteId y sumar totales
        Map<String, BigDecimal> acumuladoPorCliente = new HashMap<>();
        Map<String, String>     nombrePorCliente    = new HashMap<>();

        for (Map<String,Object> v : ventas) {
            if ("ANULADO".equals(v.get("estado"))) continue;
            Object clienteIdObj = v.get("clienteId");
            if (clienteIdObj == null) continue;
            String clienteId = String.valueOf(clienteIdObj);
            // Usar Objects.toString con default "0" para evitar NPE en campos null
            String totalStr = Objects.toString(v.get("total"), "0");
            if (totalStr == null || totalStr.isBlank()) totalStr = "0";
            try {
                BigDecimal total = new BigDecimal(totalStr);
                acumuladoPorCliente.merge(clienteId, total, BigDecimal::add);
            } catch (NumberFormatException e) {
                // ignorar ventas con total inválido
            }
            // Capturar nombre desde la venta
            String nombre = Objects.toString(v.get("clienteNombre"), "").trim();
            if (!nombre.isEmpty()) nombrePorCliente.put(clienteId, nombre);
        }

        // Complementar nombres desde el microservicio de clientes
        Map<String,Map<String,Object>> clientesMap = new HashMap<>();
        for (Map<String,Object> c : clientes) {
            clientesMap.put(String.valueOf(c.get("id")), c);
        }

        // Construir resultado con todos los clientes que tienen ventas
        List<Map<String,Object>> resultado = acumuladoPorCliente.entrySet().stream()
            .sorted(Map.Entry.<String,BigDecimal>comparingByValue().reversed())
            .limit(10)
            .map(e -> {
                Map<String,Object> m = new LinkedHashMap<>();
                Map<String,Object> cliente = clientesMap.get(e.getKey());
                String nombre = nombrePorCliente.getOrDefault(e.getKey(), "");
                if (nombre.isEmpty() && cliente != null) {
                    nombre = cliente.get("nombre") + " " + cliente.getOrDefault("apellido","");
                }
                m.put("nombre", nombre.trim().isEmpty() ? "Cliente #" + e.getKey() : nombre.trim());
                m.put("tipo",   cliente != null ? cliente.get("tipo") : "REGULAR");
                m.put("totalAcumulado", e.getValue());
                return m;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ventas-por-tipo")
    public ResponseEntity<?> ventasPorTipoPago() {
        List<Map<String,Object>> ventas = ventaClient.listar();
        long contado = ventas.stream().filter(v -> "CONTADO".equals(v.get("tipoPago"))).count();
        long credito = ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago"))).count();
        BigDecimal montoContado = ventas.stream().filter(v -> "CONTADO".equals(v.get("tipoPago")))
            .map(v -> new BigDecimal(v.getOrDefault("total","0").toString()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal montoCredito = ventas.stream().filter(v -> "CREDITO".equals(v.get("tipoPago")))
            .map(v -> new BigDecimal(v.getOrDefault("total","0").toString()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of(
            "contado", Map.of("cantidad", contado, "monto", montoContado),
            "credito", Map.of("cantidad", credito, "monto", montoCredito)
        ));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<?> bajoStock() {
        return ResponseEntity.ok(productoClient.bajoStock());
    }

    @GetMapping("/letras-vencidas")
    public ResponseEntity<?> letrasVencidas() {
        return ResponseEntity.ok(ventaClient.letrasVencidas());
    }

    // ── Helpers ──────────────────────────────────────────────────

    private BigDecimal sumar(List<Map<String,Object>> lista, String campo) {
        return lista.stream()
            .map(m -> {
                String val = Objects.toString(m.get(campo), "0");
                try { return new BigDecimal(val == null || val.isBlank() ? "0" : val); }
                catch (NumberFormatException e) { return BigDecimal.ZERO; }
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void acumularPorMes(Map<String,BigDecimal[]> data, String[] meses,
                                 Map<String,Object> item, int idx) {
        String fecha = Objects.toString(item.get("fecha"), "");
        if (fecha.length() >= 7) {
            try {
                int mes = Integer.parseInt(fecha.substring(5,7)) - 1;
                String totalStr = Objects.toString(item.get("total"), "0");
                BigDecimal total = new BigDecimal(totalStr == null || totalStr.isBlank() ? "0" : totalStr);
                data.get(meses[mes])[idx] = data.get(meses[mes])[idx].add(total);
            } catch (Exception e) { /* ignorar fila inválida */ }
        }
    }
}
