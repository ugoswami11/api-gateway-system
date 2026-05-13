package com.gateway.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String requestPath = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("Incoming Request -> Method: {}, Path: {}",
                method, requestPath);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    int statusCode = exchange.getResponse()
                            .getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 500;

                    long timeTaken = System.currentTimeMillis() - startTime;

                    log.info("Outgoing Response -> Status: {}, Time Taken: {} ms",
                            statusCode, timeTaken);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}