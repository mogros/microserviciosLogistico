package com.logistica.app.compras.repository;
import com.logistica.app.compras.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface CompraRepository extends JpaRepository<Compra,Long> {
    List<Compra> findByProveedorId(Long proveedorId);
    List<Compra> findByEstado(Compra.EstadoCompra estado);
    List<Compra> findByFechaBetween(LocalDate inicio, LocalDate fin);
}