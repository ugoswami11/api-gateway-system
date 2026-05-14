package com.gateway.gatewayservice.util;

import com.gateway.gatewayservice.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final ObjectMapper objectMapper;

    public Mono<Void> buildErrorResponse(
            ServerWebExchange exchange,
            HttpStatus status,
            String message) {

        try {

            ErrorResponse errorResponse = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    exchange.getRequest()
                            .getURI()
                            .getPath()
            );

            byte[] responseBytes =
                    objectMapper.writeValueAsBytes(errorResponse);

            exchange.getResponse()
                    .setStatusCode(status);

            exchange.getResponse()
                    .getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse()
                    .writeWith(
                            Mono.just(
                                    exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(responseBytes)
                            )
                    );

        } catch (Exception ex) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

            return exchange.getResponse().setComplete();
        }
    }
}