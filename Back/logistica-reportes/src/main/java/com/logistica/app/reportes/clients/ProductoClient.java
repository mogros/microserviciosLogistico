package com.logistica.app.reportes.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name="logistica-productos", configuration=com.logistica.commons.config.FeignPropagationConfig.class)
public interface ProductoClient {
    @GetMapping
    List<Map<String,Object>> listar();
    @GetMapping("/bajo-stock")
    List<Map<String,Object>> bajoStock();
}