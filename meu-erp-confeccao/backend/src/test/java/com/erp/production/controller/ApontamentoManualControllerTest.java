package com.erp.production.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.core.tenant.EmpresaContext;
import com.erp.production.domain.ApontamentoManual;
import com.erp.production.dto.ApontamentoManualRequest;
import com.erp.production.repository.ApontamentoManualRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApontamentoManualControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApontamentoManualRepository repository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ApontamentoManualController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        EmpresaContext.clear();
    }

    @AfterEach
    void tearDown() {
        EmpresaContext.clear();
    }

    @Test
    void shouldCriarApontamentoManual() throws Exception {
        UUID funcId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        
        ApontamentoManualRequest request = new ApontamentoManualRequest(funcId, 30, "Limpeza");

        Funcionario func = new Funcionario();
        func.setId(funcId);
        
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        ApontamentoManual saved = new ApontamentoManual();
        saved.setId(UUID.randomUUID());
        saved.setMinutos(30);
        saved.setObservacao("Limpeza");
        saved.setFuncionario(func);

        when(funcionarioRepository.findById(funcId)).thenReturn(Optional.of(func));
        EmpresaContext.setEmpresas(List.of(empresaId));
        when(entityManager.getReference(Empresa.class, empresaId)).thenReturn(empresa);
        when(repository.save(any(ApontamentoManual.class))).thenReturn(saved);

        mockMvc.perform(post("/api/production/apontamentos-manuais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minutos").value(30))
                .andExpect(jsonPath("$.observacao").value("Limpeza"));

        verify(repository, times(1)).save(any(ApontamentoManual.class));
    }

    @Test
    void shouldThrowExceptionWhenFuncionarioNotFound() throws Exception {
        UUID funcId = UUID.randomUUID();
        ApontamentoManualRequest request = new ApontamentoManualRequest(funcId, 30, "Limpeza");

        when(funcionarioRepository.findById(funcId)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            mockMvc.perform(post("/api/production/apontamentos-manuais")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
        ).hasCauseInstanceOf(IllegalArgumentException.class);
    }
}
