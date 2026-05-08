package com.logistica.app.ventas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonIgnore rompe la referencia circular:
    // Venta → detalles → DetalleVenta → venta → detalles → ...
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 200)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta v) { this.venta = v; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long p) { this.productoId = p; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String n) { this.nombreProducto = n; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer c) { this.cantidad = c; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p) { this.precioUnitario = p; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal s) { this.subtotal = s; }
}
