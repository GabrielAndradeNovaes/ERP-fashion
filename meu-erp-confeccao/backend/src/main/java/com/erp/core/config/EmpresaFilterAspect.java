package com.erp.core.config;

import com.erp.core.tenant.EmpresaContext;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Aspect
@Component
public class EmpresaFilterAspect {

    private final EntityManager entityManager;

    public EmpresaFilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("execution(* com.erp..*Repository+.*(..))")
    public void enableEmpresaFilter() {
        List<UUID> empresaIds = EmpresaContext.getEmpresas();
        if (empresaIds != null && !empresaIds.isEmpty()) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("empresaFilter").setParameterList("empresaIds", empresaIds);
        }
    }
}
