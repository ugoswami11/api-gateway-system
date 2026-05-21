package com.gateway.gatewayservice.filter;

import com.gateway.gatewayservice.constants.SecurityConstants;
import com.gateway.gatewayservice.service.TokenBucketRateLimiterService;
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
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final ResponseUtil responseUtil;
    private final TokenBucketRateLimiterService rateLimiterService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        /*
         * Skip Public Routes
         */
        if (path.contains(SecurityConstants.LOGIN_PATH)
                || path.contains(SecurityConstants.REGISTER_PATH)) {

            return chain.filter(exchange);
        }

        String username =
                exchange.getAttribute("username");

        if (username == null) {
            return responseUtil.buildErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "User authentication failed"
            );
        }

        /*
         * Execute Redis Token Bucket Check
         */
        return rateLimiterService.isAllowed(username, path)
                .flatMap(allowed -> {

                    if (!allowed) {

                        log.warn("Rate limit exceeded for user: {}", username);

                        return responseUtil.buildErrorResponse(
                                exchange,
                                HttpStatus.TOO_MANY_REQUESTS,
                                "Rate limit exceeded. Please try again later."
                        );
                    }

                    /*
                     * Allowed → Forward Request
                     */
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}