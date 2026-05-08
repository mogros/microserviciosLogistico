package com.logistica.app.compras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "letras_compra")
public class LetraCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @Column(nullable = false)
    private Integer numeroLetra;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(precision = 12, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoLetra estado = EstadoLetra.PENDIENTE;

    @Column
    private LocalDate fechaPago;

    public enum EstadoLetra { PENDIENTE, PAGADO, VENCIDO }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Compra getCompra() { return compra; }
    public void setCompra(Compra c) { this.compra = c; }
    public Integer getNumeroLetra() { return numeroLetra; }
    public void setNumeroLetra(Integer n) { this.numeroLetra = n; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate f) { this.fechaVencimiento = f; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal m) { this.monto = m; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal m) { this.montoPagado = m; }
    public EstadoLetra getEstado() { return estado; }
    public void setEstado(EstadoLetra e) { this.estado = e; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate f) { this.fechaPago = f; }
}
