package com.gateway.gatewayservice.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}