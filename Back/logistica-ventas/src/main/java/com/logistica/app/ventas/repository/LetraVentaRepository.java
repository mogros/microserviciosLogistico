package com.logistica.app.ventas.repository;
import com.logistica.app.ventas.entity.LetraVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface LetraVentaRepository extends JpaRepository<LetraVenta,Long> {
    List<LetraVenta> findByVentaId(Long ventaId);
    //List<LetraVenta> findByEstado(LetraVenta.EstadoLetra estado);
    List<LetraVenta> findByFechaVencimientoBeforeAndEstado(LocalDate fecha, LetraVenta.EstadoLetra estado);
    // Busca directamente por estado VENCIDO (letras ya procesadas por el scheduler)
    List<LetraVenta> findByEstado(LetraVenta.EstadoLetra estado);
}