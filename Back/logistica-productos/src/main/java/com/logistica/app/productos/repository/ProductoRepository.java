package com.logistica.app.productos.repository;
import com.logistica.app.productos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto,Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.puntoReorden AND p.activo = true")
    List<Producto> findProductosBajoStock();
    
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
}