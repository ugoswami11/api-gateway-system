package com.gateway.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public DefaultRedisScript<List> rateLimiterScript() {

        DefaultRedisScript<List> redisScript =
                new DefaultRedisScript<>();

        redisScript.setLocation(
                new ClassPathResource("scripts/token-bucket.lua")
        );

        redisScript.setResultType(List.class);

        return redisScript;
    }
}