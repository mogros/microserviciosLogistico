package com.logistica.app.productos.service;
import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.productos.entity.Producto;
import com.logistica.app.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductoServiceImpl extends CommonServiceImpl<Producto,ProductoRepository> implements ProductoService {
    public java.util.List<Producto> findByNombre(String n){return repository.findByNombreContainingIgnoreCase(n);}
    public java.util.List<Producto> findBajoStock(){return repository.findProductosBajoStock();}
    public Producto actualizarStock(Long id, int cantidad){
        Producto p = repository.findById(id).orElseThrow(()->new RuntimeException("Producto no encontrado: "+id));
        p.setStock(p.getStock()+cantidad);
        return repository.save(p);
    }
}