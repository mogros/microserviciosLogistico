package com.logistica.app.compras.dto;

import java.math.BigDecimal;
import java.util.List;

public class CompraRequest {
    private Long proveedorId;
    private String proveedorNombre;
    private String tipoPago;
    private BigDecimal subtotal;
    private String observaciones;
    private int cuotas;
    private List<DetalleRequest> detalles;

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
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
