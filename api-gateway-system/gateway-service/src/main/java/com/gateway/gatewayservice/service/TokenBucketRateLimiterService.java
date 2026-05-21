package com.gateway.gatewayservice.service;

import com.gateway.gatewayservice.config.RateLimitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBucketRateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List> rateLimiterScript;
    private final RateLimitConfig rateLimitConfig;

    private static final long REFILL_RATE = 1;

    private long resolveCapacity(String path) {
        for (Map.Entry<String, Long> entry :
                rateLimitConfig.getRouteLimits().entrySet()) {

            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return rateLimitConfig.getDefaultLimit();
    }

    public Mono<Boolean> isAllowed(String userKey, String path) {

        String tokenKey = "rate_limit:" + userKey + ":tokens";
        String lastRefillKey = "rate_limit:" + userKey + ":last_refill";

        long capacity = resolveCapacity(path);
        long currentTime = System.currentTimeMillis();

        return redisTemplate.execute(
                        rateLimiterScript,
                        Arrays.asList(tokenKey, lastRefillKey),
                        String.valueOf(capacity),
                        String.valueOf(REFILL_RATE),
                        String.valueOf(currentTime)
                )
                .next()
                .map(result -> {

                    List<Long> response = (List<Long>) result;

                    Long allowed = response.get(0);

                    boolean isAllowed = allowed != null && allowed == 1;

                    if (!isAllowed) {
                        log.warn("Rate limit exceeded for user: {}", userKey);
                    }

                    return isAllowed;
                })
                .defaultIfEmpty(false);
    }
}