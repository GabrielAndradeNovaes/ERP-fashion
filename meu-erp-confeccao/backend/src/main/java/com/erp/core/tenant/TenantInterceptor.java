package com.erp.core.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import java.util.List;
import java.util.Optional;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-TenantID";

    @Value("${erp.root-domain:localhost}")
    private String rootDomain;

    @Value("${erp.platform-subdomains:admin,www,api,app}")
    private List<String> platformSubdomains;

    @Autowired
    @Lazy
    private TenantRepository tenantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String host = request.getServerName(); // e.g. "empresa1.localhost" or "admin.localhost"
        String resolvedTenantId = null;

        // Salvar contexto atual (definido pelo JwtAuthFilter) antes de alterar
        String jwtTenant = TenantContext.getCurrentTenant();

        // 1. Tentar resolver por subdomínio
        if (host != null && host.endsWith("." + rootDomain)) {
            String subdomain = host.substring(0, host.indexOf("." + rootDomain));
            if (!platformSubdomains.contains(subdomain.toLowerCase())) {
                // Para buscar por slug com segurança, garantimos que o contexto esteja como master 
                // caso contrário o TenantRoutingDataSource pode tentar usar um schema inválido.
                TenantContext.setCurrentTenant(TenantContext.MASTER_TENANT);
                Optional<Tenant> tenantOpt = tenantRepository.findBySlug(subdomain.toLowerCase());
                if (tenantOpt.isPresent()) {
                    resolvedTenantId = tenantOpt.get().getSchemaName();
                }
                // Restauramos o contexto JWT temporariamente
                TenantContext.setCurrentTenant(jwtTenant);
            }
        }

        // 2. Fallback para header
        if (resolvedTenantId == null) {
            String headerTenantId = request.getHeader(TENANT_HEADER);
            if (headerTenantId != null && !headerTenantId.trim().isEmpty()) {
                TenantContext.setCurrentTenant(TenantContext.MASTER_TENANT);
                Optional<Tenant> tenantOpt = tenantRepository.findBySlug(headerTenantId.toLowerCase());
                if (tenantOpt.isPresent()) {
                    resolvedTenantId = tenantOpt.get().getSchemaName();
                } else {
                    // Pode já ser um schema_name válido
                    resolvedTenantId = headerTenantId;
                }
                TenantContext.setCurrentTenant(jwtTenant);
            }
        }

        // 3. Aplicar o tenant resolvido, se houver
        if (resolvedTenantId != null) {
            TenantContext.setCurrentTenant(resolvedTenantId);
        } else {
            // Se não resolveu via URL nem Header, mantém o que o JwtAuthenticationFilter configurou!
            // Se não tiver nada no contexto (ex: rota pública), define como master.
            if (jwtTenant == null) {
                TenantContext.setCurrentTenant(TenantContext.MASTER_TENANT);
            } else {
                TenantContext.setCurrentTenant(jwtTenant);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();
    }
}
