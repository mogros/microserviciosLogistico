package com.logistica.app.clientes.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="clientes")
public class Cliente {
	
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty @Size(max=100) 
    @Column(nullable=false) 
    private String nombre;
    
    @Size(max=100) 
    private String apellido;
    
    @NotEmpty @Size(max=20) 
    @Column(nullable=false,unique=true)
    private String ruc;
    
    @Email @Column(length=100) 
    private String email;
    
    @Column(length=20) 
    private String telefono;
    
    @Column(length=300) 
    private String direccion;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable=false,length=20)
    private TipoCliente tipo = TipoCliente.REGULAR;
    
    @DecimalMin("0.0") @Column(nullable=false,precision=12,scale=2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;
    
    @Column(nullable=false) 
    private boolean activo = true;
    
    @Column 
    private LocalDate fechaRegistro = LocalDate.now();
    
    @Column(nullable=false,precision=12,scale=2)
    private BigDecimal totalComprasAcumuladas = BigDecimal.ZERO;

    public enum TipoCliente { REGULAR, PREMIUM, VIP }

    public Long getId(){return id;} 
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;} 
    public void setNombre(String n){this.nombre=n;}
    public String getApellido(){return apellido;}
    public void setApellido(String a){this.apellido=a;}
    public String getRuc(){return ruc;} 
    public void setRuc(String r){this.ruc=r;}
    public String getEmail(){return email;}
    public void setEmail(String e){this.email=e;}
    public String getTelefono(){return telefono;} 
    public void setTelefono(String t){this.telefono=t;}
    public String getDireccion(){return direccion;} 
    public void setDireccion(String d){this.direccion=d;}
    public TipoCliente getTipo(){return tipo;}
    public void setTipo(TipoCliente t){this.tipo=t;}
    public BigDecimal getLimiteCredito(){return limiteCredito;} 
    public void setLimiteCredito(BigDecimal l){this.limiteCredito=l;}
    public boolean isActivo(){return activo;} 
    public void setActivo(boolean a){this.activo=a;}
    public LocalDate getFechaRegistro(){return fechaRegistro;} 
    public void setFechaRegistro(LocalDate f){this.fechaRegistro=f;}
    public BigDecimal getTotalComprasAcumuladas(){return totalComprasAcumuladas;} 
    public void setTotalComprasAcumuladas(BigDecimal t){this.totalComprasAcumuladas=t;}
}