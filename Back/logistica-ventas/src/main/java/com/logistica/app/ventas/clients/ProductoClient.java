package com.logistica.app.ventas.clients;

import com.logistica.commons.config.FeignPropagationConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "logistica-productos", configuration = FeignPropagationConfig.class)
public interface ProductoClient {

    /** Obtener producto por ID para verificar stock disponible */
    @GetMapping("/{id}")
    Map<String, Object> obtenerProducto(@PathVariable Long id);

    /**
     * Actualiza el stock del producto.
     * cantidad negativa = reducir (venta)
     * cantidad positiva = incrementar (compra/devolución)
     */
    @PutMapping("/{id}/stock")
    Map<String, Object> actualizarStock(@PathVariable Long id, @RequestParam int cantidad);
}
