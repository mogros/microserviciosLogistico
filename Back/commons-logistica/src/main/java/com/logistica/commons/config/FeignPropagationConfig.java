package com.logistica.commons.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor Feign que propaga el header X-Auth-Username
 * a todas las llamadas entre microservicios.
 *
 * Cuando ms-A llama a ms-B via Feign, ms-B tiene GatewayHeaderFilter
 * que exige este header. Sin este interceptor ms-B devolvería 401.
 */
@Configuration
public class FeignPropagationConfig {

    @Bean
    public RequestInterceptor propagarHeaderAutenticacion() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    String username = attrs.getRequest().getHeader("X-Auth-Username");
                    if (username != null && !username.isBlank()) {
                        template.header("X-Auth-Username", username);
                    }
                }
            }
        };
    }
}
