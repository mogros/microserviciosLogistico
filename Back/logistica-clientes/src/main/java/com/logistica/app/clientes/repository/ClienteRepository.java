package com.logistica.app.clientes.repository;
import com.logistica.app.clientes.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    List<Cliente> findByNombreContainingIgnoreCaseOrRucContaining(String nombre, String ruc);
    List<Cliente> findByTipo(Cliente.TipoCliente tipo);
}