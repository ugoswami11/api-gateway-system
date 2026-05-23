package com.gateway.gatewayservice.controller;

import com.gateway.gatewayservice.dto.FallbackResponse;
import com.gateway.gatewayservice.filter.CorrelationIdFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/users")
    public Mono<ResponseEntity<FallbackResponse>> userFallback(
            org.springframework.web.server.ServerWebExchange exchange
    ) {

        String correlationId =
                exchange.getAttribute(
                        CorrelationIdFilter.CORRELATION_ID
                );

        FallbackResponse response =
                FallbackResponse.builder()
                        .timestamp(Instant.now().toString())
                        .status(503)
                        .error("SERVICE_UNAVAILABLE")
                        .message("User Service is temporarily unavailable")
                        .service("USER-SERVICE")
                        .path(exchange.getRequest().getPath().value())
                        .correlationId(correlationId)
                        .build();

        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(response)
        );
    }
}