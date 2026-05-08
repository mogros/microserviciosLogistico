package com.logistica.app.reportes.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@FeignClient(name="logistica-ventas", configuration=com.logistica.commons.config.FeignPropagationConfig.class)
public interface VentaClient {
    @GetMapping("/periodo")
    List<Map<String,Object>> findByPeriodo(@RequestParam String inicio, @RequestParam String fin);
    @GetMapping
    List<Map<String,Object>> listar();
    @GetMapping("/letras/vencidas")
    List<Map<String,Object>> letrasVencidas();
}