package com.logistica.app.clientes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient
public class LogisticaClientesApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaClientesApplication.class, args); }
}