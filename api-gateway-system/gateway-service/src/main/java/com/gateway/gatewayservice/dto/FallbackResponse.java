package com.gateway.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FallbackResponse {

    private String timestamp;

    private int status;

    private String error;

    private String message;

    private String service;

    private String path;

    private String correlationId;
}