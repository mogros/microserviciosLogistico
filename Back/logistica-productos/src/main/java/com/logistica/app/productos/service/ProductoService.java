package com.logistica.app.productos.service;
import com.logistica.commons.service.CommonService;
import com.logistica.app.productos.entity.Producto;
import java.util.List;
public interface ProductoService extends CommonService<Producto> {
    List<Producto> findByNombre(String nombre);
    List<Producto> findBajoStock();
    Producto actualizarStock(Long id, int cantidad);
}