package com.logistica.app.auth.model;
import jakarta.persistence.*;

@Entity @Table(name = "roles")
public class Rol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer id;
    
    @Enumerated(EnumType.STRING) 
    @Column(length = 20, nullable = false, unique = true)
    private NombreRol nombre;
    
    public Rol() {}
    public Rol(NombreRol nombre) { this.nombre = nombre; }
    
    public Integer getId() { 
    	return id; 
    	} 
    
    public void setId(Integer id) { this.id = id; }
    
    public NombreRol getNombre() { return nombre; } 
    
    public void setNombre(NombreRol nombre) { this.nombre = nombre; }
    
    public enum NombreRol { ROLE_ADMIN, ROLE_VENDEDOR, ROLE_COMPRADOR }
    
}