package com.erp.core.security;

import com.erp.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String tenantId;

        // Se não tem header Bearer, continua a cadeia (será bloqueado pelo Spring Security se a rota for protegida)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // O TenantInterceptor lidará com o cabeçalho X-TenantID ou subdomínio se necessário.
            // Aqui deixamos como null para o TenantInterceptor processar.
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
            tenantId = jwtService.extractTenantId(jwt);
            java.util.List<String> empresas = jwtService.extractEmpresas(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Seta o Tenant no contexto a partir do Token!
                    TenantContext.setCurrentTenant(tenantId);
                    
                    if (empresas != null && !empresas.isEmpty()) {
                        com.erp.core.tenant.EmpresaContext.setEmpresas(
                            empresas.stream().map(java.util.UUID::fromString).collect(java.util.stream.Collectors.toList())
                        );
                    }

                    // Se for SUPERADMIN, o TenantInterceptor vai lidar com o cabeçalho X-TenantID ou o subdomínio.
                    // Nós não sobrescrevemos o TenantContext aqui com o cabeçalho porque o cabeçalho pode ser um slug
                    // e o TenantContext deve armazenar o schema_name.
                }
            }
        } catch (Exception e) {
            // Token inválido ou expirado
            System.err.println("Erro ao validar token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
        TenantContext.clear();
        com.erp.core.tenant.EmpresaContext.clear();
    }
}
