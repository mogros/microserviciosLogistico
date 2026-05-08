package com.logistica.app.proveedores.repository;
import com.logistica.app.proveedores.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProveedorRepository extends JpaRepository<Proveedor,Long> {
    List<Proveedor> findByRazonSocialContainingIgnoreCaseOrRucContaining(String razon, String ruc);
}