package com.logistica.app.compras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

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
    public Compra getCompra() { return compra; }
    public void setCompra(Compra c) { this.compra = c; }
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
