package com.logistica.commons.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que valida que la peticion provenga del Gateway.
 * El Gateway añade X-Auth-Username despues de validar el JWT.
 * Las peticiones OPTIONS (preflight CORS) se dejan pasar sin validar.
 */
public class GatewayHeaderFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderFilter.class);
    public static final String HEADER_USERNAME = "X-Auth-Username";
    public static final String HEADER_ROLES    = "X-Auth-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        // OPTIONS preflight — dejar pasar sin validar
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String username = req.getHeader(HEADER_USERNAME);

        if (username == null || username.isBlank()) {
            log.warn("Acceso rechazado sin header X-Auth-Username: {} {}", req.getMethod(), req.getRequestURI());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Acceso directo al microservicio no permitido\"}");
            return;
        }

        // Construir authorities desde header de roles (opcional)
        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        String rolesHeader = req.getHeader(HEADER_ROLES);
        if (rolesHeader != null && !rolesHeader.isBlank()) {
            authorities = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // Registrar autenticacion en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, authorities));

        log.debug("Peticion autenticada: {} {} — usuario: {}", req.getMethod(), req.getRequestURI(), username);
        chain.doFilter(req, res);
    }
}
