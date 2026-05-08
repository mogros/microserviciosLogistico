package com.logistica.app.clientes.service;
import com.logistica.commons.service.CommonService;
import com.logistica.app.clientes.entity.Cliente;
import java.math.BigDecimal;
import java.util.List;
public interface ClienteService extends CommonService<Cliente> {
    List<Cliente> buscar(String texto);
    List<Cliente> findByTipo(Cliente.TipoCliente tipo);
    Cliente actualizarAcumulado(Long id, BigDecimal monto);
    Cliente evaluarYActualizarTipo(Long id);
}