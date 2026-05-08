package com.logistica.app.ventas.repository;
import com.logistica.app.ventas.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface VentaRepository extends JpaRepository<Venta,Long> {
    Optional<Venta> findByNumeroVenta(String numero);
    List<Venta> findByClienteId(Long clienteId);
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    List<Venta> findByFechaBetween(LocalDate inicio, LocalDate fin);
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio")
    Long countVentasDesde(LocalDate inicio);
}