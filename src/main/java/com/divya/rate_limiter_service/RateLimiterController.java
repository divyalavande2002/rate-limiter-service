package com.divya.rate_limiter_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
public class RateLimiterController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_SECONDS = 60;

    @GetMapping("/api/resource")
    public ResponseEntity<String> accessResource(@RequestParam String userId) {
        String key = "rate_limit:" + userId;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        if (currentCount > MAX_REQUESTS) {
            return ResponseEntity.status(429).body("Rate limit exceeded. Try again later.");
        }

        return ResponseEntity.ok("Request successful. Count: " + currentCount + "/" + MAX_REQUESTS);
    }
}