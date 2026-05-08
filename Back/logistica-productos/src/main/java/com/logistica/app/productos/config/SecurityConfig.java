package com.logistica.app.productos.config;
import com.logistica.commons.security.MicroservicioSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration 
@EnableWebSecurity
public class SecurityConfig extends MicroservicioSecurityConfig{}