package com.erp.core.config;

import com.erp.core.tenant.EmpresaContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaFilterAspectTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    @Mock
    private Filter filter;

    private EmpresaFilterAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new EmpresaFilterAspect(entityManager);
        EmpresaContext.clear();
    }

    @AfterEach
    void tearDown() {
        EmpresaContext.clear();
    }

    @Test
    void enableEmpresaFilter_WithEmpresas_EnablesFilter() {
        UUID empresaId = UUID.randomUUID();
        EmpresaContext.setEmpresas(Collections.singletonList(empresaId));
        when(entityManager.unwrap(org.mockito.ArgumentMatchers.any())).thenReturn(session);
        when(session.enableFilter(org.mockito.ArgumentMatchers.anyString())).thenReturn(filter);

        aspect.enableEmpresaFilter();

        verify(entityManager).unwrap(Session.class);
        verify(session).enableFilter("empresaFilter");
        verify(filter).setParameterList("empresaIds", Collections.singletonList(empresaId));
    }

    @Test
    void enableEmpresaFilter_WithoutEmpresas_DoesNothing() {
        EmpresaContext.clear();

        aspect.enableEmpresaFilter();

        verify(entityManager, never()).unwrap(Session.class);
    }
}
