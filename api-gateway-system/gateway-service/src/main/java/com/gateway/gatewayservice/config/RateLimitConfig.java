package com.gateway.gatewayservice.config;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RateLimitConfig {

    /*
     * Route → Capacity Mapping
     */
    public Map<String, Long> getRouteLimits() {

        Map<String, Long> routeLimits = new LinkedHashMap<>();

        routeLimits.put("/auth/login", 5L);
        routeLimits.put("/api/payments", 20L);
        routeLimits.put("/api/orders", 50L);
        routeLimits.put("/api/users", 100L);

        return routeLimits;
    }

    /*
     * Default fallback limit
     */
    public long getDefaultLimit() {
        return 30L;
    }

    /*
     * Refill Rate Per Second
     */
    public long getRefillRate() {
        return 1L;
    }
}