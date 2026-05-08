package com.logistica.app.productos.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity @Table(name="productos")
public class Producto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;
    
    @NotEmpty @Size(max=200) @Column(nullable=false) 
    private String nombre;
    
    @Column(length=500) 
    private String descripcion;
    
    @NotNull 
    @DecimalMin("0.0") 
    @Column(nullable=false,precision=12,scale=2) 
    private BigDecimal precioVenta;
    
    @NotNull 
    @DecimalMin("0.0") 
    @Column(nullable=false,precision=12,scale=2) 
    private BigDecimal precioCompra;
    
    @NotNull 
    @Min(0) 
    @Column(nullable=false) 
    private Integer stock = 0;
    
    @NotNull
    @Min(0) 
    @Column(nullable=false) 
    private Integer puntoReorden = 5;
    
    @Column(length=50) 
    private String unidadMedida;
    
    @Column(nullable=false) 
    private boolean activo = true;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="categoria_id") 
    private Categoria categoria;

    public Long getId(){return id;} 
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;} 
    public void setNombre(String n){this.nombre=n;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String d){this.descripcion=d;}
    public BigDecimal getPrecioVenta(){return precioVenta;}
    public void setPrecioVenta(BigDecimal p){this.precioVenta=p;}
    public BigDecimal getPrecioCompra(){return precioCompra;} 
    public void setPrecioCompra(BigDecimal p){this.precioCompra=p;}
    public Integer getStock(){return stock;} public void setStock(Integer s){this.stock=s;}
    public Integer getPuntoReorden(){return puntoReorden;} 
    public void setPuntoReorden(Integer p){this.puntoReorden=p;}
    public String getUnidadMedida(){return unidadMedida;} 
    public void setUnidadMedida(String u){this.unidadMedida=u;}
    public boolean isActivo(){return activo;} 
    public void setActivo(boolean a){this.activo=a;}
    public Categoria getCategoria(){return categoria;} 
    public void setCategoria(Categoria c){this.categoria=c;}
    public boolean isBajoStock(){return stock != null && puntoReorden != null && stock <= puntoReorden;}
}