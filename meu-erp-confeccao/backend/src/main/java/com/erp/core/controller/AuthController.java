package com.erp.core.controller;

import com.erp.core.security.AuthService;
import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final com.erp.core.security.LoginRateLimiterService rateLimiterService;

    public AuthController(AuthService authService, com.erp.core.security.LoginRateLimiterService rateLimiterService) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        
        // Verifica o rate limit antes de tentar autenticar
        rateLimiterService.checkAndIncrement(clientIp);
        
        AuthResponse response = authService.authenticate(request);
        
        // Se a autenticação passar sem exceptions, reseta o contador
        rateLimiterService.reset(clientIp);
        
        return ResponseEntity.ok(response);
    }
}
