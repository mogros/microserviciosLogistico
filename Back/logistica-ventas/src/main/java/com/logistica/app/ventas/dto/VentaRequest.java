package com.logistica.app.ventas.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para recibir la peticion de crear venta desde el frontend.
 * Evita el problema de referencia circular al deserializar con ObjectMapper.
 */
public class VentaRequest {

    private Long clienteId;
    private String clienteNombre;
    private String tipoPago;       // "CONTADO" o "CREDITO"
    private BigDecimal subtotal;
    private String observaciones;
    private int cuotas;
    private List<DetalleRequest> detalles;

    // ── Getters y Setters ──────────────────────────────────────────

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getCuotas() { return cuotas; }
    public void setCuotas(int cuotas) { this.cuotas = cuotas; }

    public List<DetalleRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRequest> detalles) { this.detalles = detalles; }

    // ── DTO interno para cada línea de detalle ─────────────────────

    public static class DetalleRequest {
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
