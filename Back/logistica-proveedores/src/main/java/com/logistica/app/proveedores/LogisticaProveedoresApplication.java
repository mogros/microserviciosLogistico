package com.logistica.app.proveedores;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication @EnableDiscoveryClient @EnableFeignClients
public class LogisticaProveedoresApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaProveedoresApplication.class, args); }
}