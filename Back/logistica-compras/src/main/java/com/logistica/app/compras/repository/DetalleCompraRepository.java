package com.logistica.app.compras.repository;

import com.logistica.app.compras.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {
}
