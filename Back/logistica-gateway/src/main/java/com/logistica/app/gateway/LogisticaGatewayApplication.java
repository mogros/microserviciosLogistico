package com.logistica.app.gateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LogisticaGatewayApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaGatewayApplication.class, args); }
}