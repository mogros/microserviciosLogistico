package com.logistica.app.compras.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="compras")
public class Compra {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=20) private String numeroCompra;
    @Column(nullable=false) private Long proveedorId;
    @Column(length=200) private String proveedorNombre;
    @Column(nullable=false) private LocalDate fecha = LocalDate.now();
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TipoPago tipoPago = TipoPago.CONTADO;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoCompra estado = EstadoCompra.PENDIENTE;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal igv = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal total = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal montoPagado = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal saldoPendiente = BigDecimal.ZERO;
    @Column(length=500) private String observaciones;
    @OneToMany(mappedBy="compra",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<DetalleCompra> detalles = new ArrayList<>();
    @OneToMany(mappedBy="compra",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<LetraCompra> letras = new ArrayList<>();
    public enum TipoPago { CONTADO, CREDITO }
    public enum EstadoCompra { PENDIENTE, PARCIAL, PAGADO, ANULADO }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNumeroCompra(){return numeroCompra;} public void setNumeroCompra(String n){this.numeroCompra=n;}
    public Long getProveedorId(){return proveedorId;} public void setProveedorId(Long p){this.proveedorId=p;}
    public String getProveedorNombre(){return proveedorNombre;} public void setProveedorNombre(String p){this.proveedorNombre=p;}
    public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate f){this.fecha=f;}
    public TipoPago getTipoPago(){return tipoPago;} public void setTipoPago(TipoPago t){this.tipoPago=t;}
    public EstadoCompra getEstado(){return estado;} public void setEstado(EstadoCompra e){this.estado=e;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal s){this.subtotal=s;}
    public BigDecimal getIgv(){return igv;} public void setIgv(BigDecimal i){this.igv=i;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal t){this.total=t;}
    public BigDecimal getMontoPagado(){return montoPagado;} public void setMontoPagado(BigDecimal m){this.montoPagado=m;}
    public BigDecimal getSaldoPendiente(){return saldoPendiente;} public void setSaldoPendiente(BigDecimal s){this.saldoPendiente=s;}
    public String getObservaciones(){return observaciones;} public void setObservaciones(String o){this.observaciones=o;}
    public List<DetalleCompra> getDetalles(){return detalles;} public void setDetalles(List<DetalleCompra> d){this.detalles=d;}
    public List<LetraCompra> getLetras(){return letras;} public void setLetras(List<LetraCompra> l){this.letras=l;}
}