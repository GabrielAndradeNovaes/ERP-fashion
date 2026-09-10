package com.erp.production.controller;

import com.erp.production.dto.ProdutividadeResumo;
import com.erp.production.service.ProdutividadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutividadeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProdutividadeService produtividadeService;

    @InjectMocks
    private ProdutividadeController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldGetResumo() throws Exception {
        UUID funcId = UUID.randomUUID();
        ProdutividadeResumo resumo = new ProdutividadeResumo(
                funcId,
                "João",
                120L,
                new BigDecimal("30.00"),
                new BigDecimal("150.00"),
                new BigDecimal("50.00"),
                100
        );

        when(produtividadeService.getResumo(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(resumo));

        mockMvc.perform(get("/api/production/produtividade")
                .param("start", "2026-09-01T00:00:00")
                .param("end", "2026-09-30T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].funcionarioId").value(funcId.toString()))
                .andExpect(jsonPath("$[0].funcionarioNome").value("João"))
                .andExpect(jsonPath("$[0].totalCupons").value(120))
                .andExpect(jsonPath("$[0].tempoPadraoProduzido").value(30.0))
                .andExpect(jsonPath("$[0].metaMinima").value(150.0))
                .andExpect(jsonPath("$[0].premio100").value(50.0))
                .andExpect(jsonPath("$[0].tempoTeorico").value(100));

        verify(produtividadeService, times(1)).getResumo(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldPagar() throws Exception {
        UUID funcId = UUID.randomUUID();
        ProdutividadeController.PagamentoRequest pag = new ProdutividadeController.PagamentoRequest(funcId, new BigDecimal("100.00"));
        
        doNothing().when(produtividadeService).pagarProdutividade(eq(funcId), any(LocalDateTime.class), any(LocalDateTime.class), eq(new BigDecimal("100.00")));

        mockMvc.perform(post("/api/production/produtividade/pagar")
                .param("start", "2026-09-01T00:00:00")
                .param("end", "2026-09-30T23:59:59")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(pag))))
                .andExpect(status().isOk());

        verify(produtividadeService, times(1)).pagarProdutividade(eq(funcId), any(LocalDateTime.class), any(LocalDateTime.class), eq(new BigDecimal("100.00")));
    }
}
