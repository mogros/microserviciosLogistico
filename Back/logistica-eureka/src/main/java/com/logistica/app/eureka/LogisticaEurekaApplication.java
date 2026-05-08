package com.logistica.app.eureka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LogisticaEurekaApplication {
    public static void main(String[] args) { SpringApplication.run(LogisticaEurekaApplication.class, args); }
}