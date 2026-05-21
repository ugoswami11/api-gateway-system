package com.gateway.gatewayservice.filter;

import com.gateway.gatewayservice.constants.SecurityConstants;
import com.gateway.gatewayservice.util.JwtUtil;
import com.gateway.gatewayservice.util.ResponseUtil;
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
    private final ResponseUtil responseUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        log.info("AuthenticationFilter executed");

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

            return responseUtil.buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid JWT token"
            );
        }

        /*
         * Extract Token
         */
        String token = authHeader.substring(7);

        try {

            /*
             * Validate JWT signature + expiration
             */
            if (!jwtUtil.validateToken(token)) {

                log.error("Invalid JWT token");

                return responseUtil.buildErrorResponse(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid or expired JWT token"
                );
            }

            /*
             * Extract username after validation
             */
            String username = jwtUtil.extractUsername(token);

            /*
             * Store username for RateLimitingFilter
             */
            exchange.getAttributes().put("username", username);

        } catch (Exception ex) {

            log.error("JWT validation failed", ex);

            return responseUtil.buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired JWT token"
            );
        }



        /*
         * Token Valid → Forward Request
         */
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3;
    }
}