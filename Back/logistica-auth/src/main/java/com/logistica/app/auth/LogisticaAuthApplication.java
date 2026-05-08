package com.logistica.app.auth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication @EnableDiscoveryClient
public class LogisticaAuthApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaAuthApplication.class, args); }
}