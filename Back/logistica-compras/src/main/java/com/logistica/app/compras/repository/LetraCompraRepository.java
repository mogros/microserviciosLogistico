package com.logistica.app.compras.repository;
import com.logistica.app.compras.entity.LetraCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface LetraCompraRepository extends JpaRepository<LetraCompra,Long> {
    List<LetraCompra> findByCompraId(Long compraId);
    List<LetraCompra> findByFechaVencimientoBeforeAndEstado(LocalDate fecha, LetraCompra.EstadoLetra estado);
}