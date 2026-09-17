package com.erp.core.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginRateLimiterService {

    private final StringRedisTemplate redisTemplate;
    
    // Configurações: 5 tentativas a cada 15 minutos
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(15);
    private static final String PREFIX = "login_attempts:";

    public LoginRateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkAndIncrement(String clientIp) {
        String key = PREFIX + clientIp;
        
        String attemptsStr = redisTemplate.opsForValue().get(key);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= MAX_ATTEMPTS) {
            throw new RateLimitExceededException("Muitas tentativas de login. Tente novamente mais tarde.");
        }

        if (attempts == 0) {
            redisTemplate.opsForValue().set(key, "1", LOCK_TIME);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
    }

    public void reset(String clientIp) {
        String key = PREFIX + clientIp;
        redisTemplate.delete(key);
    }
}
