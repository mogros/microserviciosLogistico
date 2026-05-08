package com.logistica.app.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.public-paths}")
    private String publicPathsRaw;

    private List<String> publicPaths;

    @PostConstruct
    public void init() {
        publicPaths = Arrays.stream(publicPathsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (publicPaths.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        if (token == null) {
            return unauthorized(exchange, "Token no proporcionado");
        }

        Claims claims;
        try {
            claims = parseToken(token);
        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "Token expirado");
        } catch (JwtException e) {
            return unauthorized(exchange, "Token invalido");
        }

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Auth-Username", claims.getSubject())
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private String extractToken(ServerHttpRequest req) {
        String h = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse res = exchange.getResponse();
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"No autorizado\",\"message\":\"" + message + "\"}";
        DataBuffer buf = res.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return res.writeWith(Mono.just(buf));
    }
}
