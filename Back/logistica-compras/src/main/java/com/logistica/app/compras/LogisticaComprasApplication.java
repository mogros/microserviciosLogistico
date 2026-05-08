package com.logistica.app.compras;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication @EnableDiscoveryClient @EnableFeignClients
public class LogisticaComprasApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaComprasApplication.class, args); }
}