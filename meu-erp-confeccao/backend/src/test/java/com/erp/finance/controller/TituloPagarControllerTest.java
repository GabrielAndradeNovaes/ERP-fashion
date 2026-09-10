package com.erp.finance.controller;

import com.erp.finance.domain.TituloPagar;
import com.erp.finance.service.FinanceiroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TituloPagarControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinanceiroService financeiroService;

    @InjectMocks
    private TituloPagarController tituloPagarController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tituloPagarController).build();
    }

    @Test
    void shouldListarTitulos() throws Exception {
        TituloPagar titulo = new TituloPagar();
        titulo.setId(UUID.randomUUID());
        titulo.setDescricao("Teste 1");

        when(financeiroService.listarTitulos()).thenReturn(List.of(titulo));

        mockMvc.perform(get("/api/financeiro/titulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(titulo.getId().toString()))
                .andExpect(jsonPath("$[0].descricao").value("Teste 1"));

        verify(financeiroService, times(1)).listarTitulos();
    }

    @Test
    void shouldBaixarTitulo() throws Exception {
        UUID id = UUID.randomUUID();
        
        doNothing().when(financeiroService).baixarTitulo(id);

        mockMvc.perform(post("/api/financeiro/titulos/" + id + "/baixar"))
                .andExpect(status().isOk());

        verify(financeiroService, times(1)).baixarTitulo(id);
    }
}
