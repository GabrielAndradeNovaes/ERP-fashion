package com.erp.core.controller;

import com.erp.core.security.AuthService;
import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private com.erp.core.security.LoginRateLimiterService rateLimiterService;

    @InjectMocks
    private AuthController controller;

    @Test
    void testLogin() {
        AuthRequest req = new AuthRequest();
        AuthResponse res = new AuthResponse("mocked_token", "Usuário Teste", "teste@empresa.com", "ADMIN", "tenant_teste", "ATIVO", null, null, null, null);
        
        jakarta.servlet.http.HttpServletRequest httpRequest = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(authService.authenticate(req)).thenReturn(res);

        ResponseEntity<AuthResponse> result = controller.login(req, httpRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }
}
