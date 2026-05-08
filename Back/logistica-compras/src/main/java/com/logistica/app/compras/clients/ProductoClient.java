package com.logistica.app.compras.clients;

import com.logistica.commons.config.FeignPropagationConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient(name = "logistica-productos", configuration = FeignPropagationConfig.class)
public interface ProductoClient {
    @PutMapping("/{id}/stock")
    Map<String, Object> actualizarStock(@PathVariable Long id, @RequestParam int cantidad);
}
