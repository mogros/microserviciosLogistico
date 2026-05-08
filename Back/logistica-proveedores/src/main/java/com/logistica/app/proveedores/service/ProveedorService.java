package com.logistica.app.proveedores.service;
import com.logistica.commons.service.CommonService;
import com.logistica.app.proveedores.entity.Proveedor;
import java.util.List;
public interface ProveedorService extends CommonService<Proveedor> {
    List<Proveedor> buscar(String texto);
}