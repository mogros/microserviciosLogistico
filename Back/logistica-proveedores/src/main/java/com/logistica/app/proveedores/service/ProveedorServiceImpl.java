package com.logistica.app.proveedores.service;
import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.proveedores.entity.Proveedor;
import com.logistica.app.proveedores.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ProveedorServiceImpl extends CommonServiceImpl<Proveedor,ProveedorRepository> implements ProveedorService {
    public List<Proveedor> buscar(String t){return repository.findByRazonSocialContainingIgnoreCaseOrRucContaining(t,t);}
}