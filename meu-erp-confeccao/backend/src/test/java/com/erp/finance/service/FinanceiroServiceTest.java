package com.erp.finance.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import com.erp.finance.domain.TituloPagar;
import com.erp.finance.repository.TituloPagarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinanceiroServiceTest {

    @Mock
    private TituloPagarRepository tituloPagarRepository;

    @InjectMocks
    private FinanceiroService financeiroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCriarTituloPagamentoFuncionario() {
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        Funcionario funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());

        TituloPagar mockTitulo = new TituloPagar();
        mockTitulo.setId(UUID.randomUUID());
        mockTitulo.setEmpresa(empresa);
        mockTitulo.setFuncionario(funcionario);
        mockTitulo.setValor(new BigDecimal("100.00"));
        mockTitulo.setDescricao("Pagamento Salario");
        mockTitulo.setStatus(TituloPagar.Status.PENDENTE);
        mockTitulo.setDataVencimento(LocalDate.now());

        when(tituloPagarRepository.save(any(TituloPagar.class))).thenReturn(mockTitulo);

        TituloPagar created = financeiroService.criarTituloPagamentoFuncionario(empresa, funcionario, new BigDecimal("100.00"), "Pagamento Salario");

        assertNotNull(created);
        assertEquals(TituloPagar.Status.PENDENTE, created.getStatus());
        assertEquals("Pagamento Salario", created.getDescricao());
        assertEquals(new BigDecimal("100.00"), created.getValor());
        
        verify(tituloPagarRepository, times(1)).save(any(TituloPagar.class));
    }

    @Test
    void shouldListarTitulos() {
        when(tituloPagarRepository.findAll()).thenReturn(List.of(new TituloPagar(), new TituloPagar()));
        
        List<TituloPagar> titulos = financeiroService.listarTitulos();
        
        assertEquals(2, titulos.size());
        verify(tituloPagarRepository, times(1)).findAll();
    }

    @Test
    void shouldBaixarTitulo() {
        UUID id = UUID.randomUUID();
        TituloPagar mockTitulo = new TituloPagar();
        mockTitulo.setId(id);
        mockTitulo.setStatus(TituloPagar.Status.PENDENTE);

        when(tituloPagarRepository.findById(id)).thenReturn(Optional.of(mockTitulo));
        when(tituloPagarRepository.save(any(TituloPagar.class))).thenReturn(mockTitulo);

        financeiroService.baixarTitulo(id);

        assertEquals(TituloPagar.Status.PAGO, mockTitulo.getStatus());
        assertNotNull(mockTitulo.getDataPagamento());
        verify(tituloPagarRepository, times(1)).findById(id);
        verify(tituloPagarRepository, times(1)).save(mockTitulo);
    }
}
