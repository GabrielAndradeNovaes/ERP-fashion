package com.erp.core.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import com.erp.core.tenant.EmpresaContext;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        String method = request != null ? request.getMethod() : "UNKNOWN";
        String uri = request != null ? request.getRequestURI() : "UNKNOWN";
        String tenant = !EmpresaContext.getEmpresas().isEmpty() ? EmpresaContext.getEmpresas().toString() : "GLOBAL";
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";

        Object result;
        try {
            result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info("REST_REQ method={} uri={} tenant={} user={} time_ms={} status=SUCCESS", method, uri, tenant, user, timeTaken);
            return result;
        } catch (IllegalArgumentException e) {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.warn("REST_REQ method={} uri={} tenant={} user={} time_ms={} status=BAD_REQUEST error=\"{}\"", method, uri, tenant, user, timeTaken, e.getMessage());
            throw e;
        } catch (Throwable e) {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("REST_REQ method={} uri={} tenant={} user={} time_ms={} status=ERROR error=\"{}\"", method, uri, tenant, user, timeTaken, e.getMessage());
            throw e;
        }
    }
}
