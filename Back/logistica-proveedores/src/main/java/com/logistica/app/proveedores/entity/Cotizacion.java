package com.logistica.app.proveedores.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="cotizaciones")
public class Cotizacion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="proveedor_id",nullable=false)
    private Proveedor proveedor;
    @Column(nullable=false) private LocalDate fecha = LocalDate.now();
    @Column private LocalDate fechaVencimiento;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;
    @Column(length=500) private String observaciones;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal total = BigDecimal.ZERO;
    @OneToMany(mappedBy="cotizacion",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    public enum EstadoCotizacion { PENDIENTE, APROBADA, RECHAZADA, VENCIDA }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Proveedor getProveedor(){return proveedor;} public void setProveedor(Proveedor p){this.proveedor=p;}
    public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate f){this.fecha=f;}
    public LocalDate getFechaVencimiento(){return fechaVencimiento;} public void setFechaVencimiento(LocalDate f){this.fechaVencimiento=f;}
    public EstadoCotizacion getEstado(){return estado;} public void setEstado(EstadoCotizacion e){this.estado=e;}
    public String getObservaciones(){return observaciones;} public void setObservaciones(String o){this.observaciones=o;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal t){this.total=t;}
    public List<DetalleCotizacion> getDetalles(){return detalles;} public void setDetalles(List<DetalleCotizacion> d){this.detalles=d;}
}