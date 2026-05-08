package com.logistica.app.productos.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="categorias")
public class Categoria {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotEmpty @Size(max=100) @Column(nullable=false,unique=true) private String nombre;
    @Column(length=255) private String descripcion;
    @Column(nullable=false) private boolean activo = true;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;} public void setNombre(String n){this.nombre=n;}
    public String getDescripcion(){return descripcion;} public void setDescripcion(String d){this.descripcion=d;}
    public boolean isActivo(){return activo;} public void setActivo(boolean a){this.activo=a;}
}