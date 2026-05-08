package com.logistica.app.ventas.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="ventas")
public class Venta {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=20) private String numeroVenta;
    @Column(nullable=false) private Long clienteId;
    @Column(length=200) private String clienteNombre;
    @Column(nullable=false) private LocalDate fecha = LocalDate.now();
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private TipoPago tipoPago = TipoPago.CONTADO;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal igv = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal total = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal montoPagado = BigDecimal.ZERO;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal saldoPendiente = BigDecimal.ZERO;
    @Column(length=500) private String observaciones;
    @OneToMany(mappedBy="venta",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    @OneToMany(mappedBy="venta",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<LetraVenta> letras = new ArrayList<>();

    public enum TipoPago { CONTADO, CREDITO }
    public enum EstadoVenta { PENDIENTE, PARCIAL, PAGADO, ANULADO }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNumeroVenta(){return numeroVenta;} public void setNumeroVenta(String n){this.numeroVenta=n;}
    public Long getClienteId(){return clienteId;} public void setClienteId(Long c){this.clienteId=c;}
    public String getClienteNombre(){return clienteNombre;} public void setClienteNombre(String c){this.clienteNombre=c;}
    public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate f){this.fecha=f;}
    public TipoPago getTipoPago(){return tipoPago;} public void setTipoPago(TipoPago t){this.tipoPago=t;}
    public EstadoVenta getEstado(){return estado;} public void setEstado(EstadoVenta e){this.estado=e;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal s){this.subtotal=s;}
    public BigDecimal getIgv(){return igv;} public void setIgv(BigDecimal i){this.igv=i;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal t){this.total=t;}
    public BigDecimal getMontoPagado(){return montoPagado;} public void setMontoPagado(BigDecimal m){this.montoPagado=m;}
    public BigDecimal getSaldoPendiente(){return saldoPendiente;} public void setSaldoPendiente(BigDecimal s){this.saldoPendiente=s;}
    public String getObservaciones(){return observaciones;} public void setObservaciones(String o){this.observaciones=o;}
    public List<DetalleVenta> getDetalles(){return detalles;} public void setDetalles(List<DetalleVenta> d){this.detalles=d;}
    public List<LetraVenta> getLetras(){return letras;} public void setLetras(List<LetraVenta> l){this.letras=l;}
}