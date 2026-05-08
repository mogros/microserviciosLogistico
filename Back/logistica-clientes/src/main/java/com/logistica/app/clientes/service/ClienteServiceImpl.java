package com.logistica.app.clientes.service;
import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.clientes.entity.Cliente;
import com.logistica.app.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteServiceImpl extends CommonServiceImpl<Cliente,ClienteRepository> implements ClienteService {
    
	public List<Cliente> buscar(String t){
		return repository.findByNombreContainingIgnoreCaseOrRucContaining(t,t);
	}
	
    public List<Cliente> findByTipo(Cliente.TipoCliente tipo){
    	return repository.findByTipo(tipo);
    }
    
    public Cliente actualizarAcumulado(Long id, BigDecimal monto){
        Cliente c = repository.findById(id).orElseThrow(()->new RuntimeException("Cliente no encontrado: "+id));
        c.setTotalComprasAcumuladas(c.getTotalComprasAcumuladas().add(monto));
        return repository.save(evaluarYActualizarTipo(c));
    }
    
    public Cliente evaluarYActualizarTipo(Long id){
        return evaluarYActualizarTipo(repository.findById(id).orElseThrow(()->new RuntimeException("Cliente no encontrado: "+id)));
    }
    
    private Cliente evaluarYActualizarTipo(Cliente c){
        BigDecimal acum = c.getTotalComprasAcumuladas();
        // Criterios premium: > 10000 = VIP, > 5000 = PREMIUM
        if (acum.compareTo(new BigDecimal("10000")) >= 0) c.setTipo(Cliente.TipoCliente.VIP);
        else if (acum.compareTo(new BigDecimal("5000")) >= 0) c.setTipo(Cliente.TipoCliente.PREMIUM);
        else c.setTipo(Cliente.TipoCliente.REGULAR);
        return repository.save(c);
    }
}