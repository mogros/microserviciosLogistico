package com.logistica.app.gateway.security;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var headers = exchange.getResponse().getHeaders();
        String origin = exchange.getRequest().getHeaders().getOrigin();
        headers.set("Access-Control-Allow-Origin", origin != null ? origin : "http://localhost:4200");
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        headers.set("Access-Control-Allow-Headers", "*");
        headers.set("Access-Control-Max-Age", "3600");
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}