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

        if (host != null && host.endsWith("." + rootDomain)) {
            String subdomain = host.substring(0, host.indexOf("." + rootDomain));
            if (!platformSubdomains.contains(subdomain.toLowerCase())) {
                Optional<Tenant> tenantOpt = tenantRepository.findBySlug(subdomain.toLowerCase());
                if (tenantOpt.isPresent()) {
                    resolvedTenantId = tenantOpt.get().getSchemaName();
                } else {
                    // Subdomain not found in tenants, maybe throw 404? 
                    // For now, let it fall back or go to master.
                }
            }
        }

        // Fallback para header caso nao tenha resolvido via subdominio
        if (resolvedTenantId == null) {
            String headerTenantId = request.getHeader(TENANT_HEADER);
            if (headerTenantId != null && !headerTenantId.trim().isEmpty()) {
                resolvedTenantId = headerTenantId;
            }
        }

        if (resolvedTenantId != null) {
            TenantContext.setCurrentTenant(resolvedTenantId);
        } else {
            TenantContext.setCurrentTenant(TenantContext.MASTER_TENANT);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();
    }
}
