package com.logistica.app.reportes.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@FeignClient(name="logistica-compras", configuration=com.logistica.commons.config.FeignPropagationConfig.class)
public interface CompraClient {
    @GetMapping("/periodo")
    List<Map<String,Object>> findByPeriodo(@RequestParam String inicio, @RequestParam String fin);
    @GetMapping
    List<Map<String,Object>> listar();
}