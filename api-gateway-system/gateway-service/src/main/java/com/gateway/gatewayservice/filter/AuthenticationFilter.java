package com.gateway.gatewayservice.filter;

import com.gateway.gatewayservice.constants.SecurityConstants;
import com.gateway.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        /*
         * Public Routes → Skip Authentication
         */
        if (path.contains(SecurityConstants.LOGIN_PATH)
                || path.contains(SecurityConstants.REGISTER_PATH)) {

            return chain.filter(exchange);
        }

        /*
         * Validate Authorization Header
         */
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null
                || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            log.error("Missing or invalid Authorization header");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        /*
         * Extract Token
         */
        String token = authHeader.replace(
                SecurityConstants.TOKEN_PREFIX,
                ""
        );

        try {

            String username = jwtUtil.extractUsername(token);

            if (!jwtUtil.validateToken(token, username)) {

                log.error("Invalid JWT token");

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

        } catch (Exception ex) {

            log.error("JWT validation failed: {}", ex.getMessage());

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        /*
         * Token Valid → Forward Request
         */
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }
}