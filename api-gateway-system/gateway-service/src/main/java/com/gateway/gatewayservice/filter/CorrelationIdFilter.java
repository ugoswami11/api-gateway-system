package com.gateway.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain
    ) {

        String correlationId = UUID.randomUUID().toString();

        ServerHttpRequest mutatedRequest =
                exchange.getRequest()
                        .mutate()
                        .header(CORRELATION_ID, correlationId)
                        .build();

        exchange.getAttributes()
                .put(CORRELATION_ID, correlationId);

        log.info("Correlation ID assigned: {}", correlationId);

        return chain.filter(
                exchange.mutate()
                        .request(mutatedRequest)
                        .build()
        );
    }

    @Override
    public int getOrder() {
        return -4;
    }
}