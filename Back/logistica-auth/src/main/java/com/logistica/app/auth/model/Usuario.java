package com.logistica.app.auth.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "usuarios_auth",
    uniqueConstraints = { @UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email") })
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotEmpty @Size(min=3,max=50) @Column(nullable=false,length=50) private String username;
    @NotEmpty @Email @Column(nullable=false,length=100) private String email;
    @NotEmpty @Column(nullable=false,length=120) private String password;
    @Column(nullable=false) private boolean enabled = true;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="usuarios_roles", joinColumns=@JoinColumn(name="usuario_id"), inverseJoinColumns=@JoinColumn(name="rol_id"))
    private Set<Rol> roles = new HashSet<>();
    public Usuario() {}
    public Usuario(String u, String e, String p) { username=u; email=e; password=p; }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean e){this.enabled=e;}
    public Set<Rol> getRoles(){return roles;} public void setRoles(Set<Rol> r){this.roles=r;}
}