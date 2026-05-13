package com.gateway.gatewayservice.controller;

import com.gateway.gatewayservice.dto.LoginRequest;
import com.gateway.gatewayservice.dto.LoginResponse;
import com.gateway.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    /*
     * In-Memory Credentials
     */
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        /*
         * Validate Credentials
         */
        if (!VALID_USERNAME.equals(request.getUsername())
                || !VALID_PASSWORD.equals(request.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        /*
         * Generate JWT Token
         */
        String token = jwtUtil.generateToken(
                request.getUsername()
        );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}