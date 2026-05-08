package com.logistica.app.reportes.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name="logistica-clientes", configuration=com.logistica.commons.config.FeignPropagationConfig.class)
public interface ClienteClient {
    @GetMapping
    List<Map<String,Object>> listar();
}