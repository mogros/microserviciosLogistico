package com.logistica.app.proveedores.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="proveedores")
public class Proveedor {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotEmpty @Size(max=200) @Column(nullable=false) private String razonSocial;
    @NotEmpty @Size(max=20) @Column(nullable=false,unique=true) private String ruc;
    @Column(length=20) private String telefono;
    @Email @Column(length=100) private String email;
    @Column(length=300) private String direccion;
    @Column(length=100) private String contacto;
    @Column(nullable=false) private boolean activo = true;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getRazonSocial(){return razonSocial;} public void setRazonSocial(String r){this.razonSocial=r;}
    public String getRuc(){return ruc;} public void setRuc(String r){this.ruc=r;}
    public String getTelefono(){return telefono;} public void setTelefono(String t){this.telefono=t;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public String getDireccion(){return direccion;} public void setDireccion(String d){this.direccion=d;}
    public String getContacto(){return contacto;} public void setContacto(String c){this.contacto=c;}
    public boolean isActivo(){return activo;} public void setActivo(boolean a){this.activo=a;}
}