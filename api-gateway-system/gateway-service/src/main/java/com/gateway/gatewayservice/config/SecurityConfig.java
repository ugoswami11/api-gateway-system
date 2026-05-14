package com.gateway.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http) {

        return http
                /*
                 * Disable CSRF for APIs
                 */
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                /*
                 * Public + Protected Routes
                 */
                .authorizeExchange(exchange -> exchange

                        /*
                         * Public Endpoints
                         */
                        .pathMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/actuator/**"
                        ).permitAll()

                        /*
                         * Protected APIs
                         */
                        .pathMatchers(HttpMethod.GET, "/api/**")
                        .authenticated()

                        .pathMatchers(HttpMethod.POST, "/api/**")
                        .authenticated()

                        .pathMatchers(HttpMethod.PUT, "/api/**")
                        .authenticated()

                        .pathMatchers(HttpMethod.DELETE, "/api/**")
                        .authenticated()

                        /*
                         * Everything Else
                         */
                        .anyExchange()
                        .authenticated()
                )

                /*
                 * Disable default login page
                 */
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .build();
    }
}