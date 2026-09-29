package com.erp.core.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimiterService {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();
    
    // Configurações: 5 tentativas, renovando 5 tentativas a cada 15 minutos
    private final Bandwidth limit;

    public LoginRateLimiterService() {
        Refill refill = Refill.intervally(5, Duration.ofMinutes(15));
        this.limit = Bandwidth.classic(5, refill);
    }

    public void checkAndIncrement(String clientIp) {
        Bucket bucket = cache.computeIfAbsent(clientIp, k -> Bucket.builder().addLimit(limit).build());
        
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Muitas tentativas de login. Tente novamente mais tarde.");
        }
    }

    public void reset(String clientIp) {
        // Remove the bucket from cache to reset limit immediately
        cache.remove(clientIp);
    }
}
