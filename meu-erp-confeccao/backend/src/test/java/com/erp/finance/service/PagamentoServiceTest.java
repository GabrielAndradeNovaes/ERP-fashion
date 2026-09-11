package com.erp.finance.service;

import com.erp.finance.domain.TituloReceber;
import com.erp.finance.domain.TransacaoPagamento;
import com.erp.finance.domain.enums.GatewayPagamento;
import com.erp.finance.domain.enums.StatusTransacao;
import com.erp.finance.gateway.PaymentGateway;
import com.erp.finance.gateway.impl.MockPaymentGatewayImpl;
import com.erp.finance.repository.TituloReceberRepository;
import com.erp.finance.repository.TransacaoPagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private TituloReceberRepository tituloRepository;

    @Mock
    private TransacaoPagamentoRepository transacaoRepository;

    private PagamentoService pagamentoService;

    @BeforeEach
    void setUp() {
        PaymentGateway mockGateway = new MockPaymentGatewayImpl();
        pagamentoService = new PagamentoService(tituloRepository, transacaoRepository, List.of(mockGateway));
    }

    @Test
    void testGerarCobrancaPixComMockGateway() {
        // Arrange
        UUID tituloId = UUID.randomUUID();
        TituloReceber titulo = new TituloReceber();
        titulo.setId(tituloId);
        titulo.setValor(new BigDecimal("150.00"));
        titulo.setDescricao("Venda Camisetas");
        titulo.setStatus("PENDING");

        when(tituloRepository.findById(tituloId)).thenReturn(Optional.of(titulo));
        when(transacaoRepository.save(any(TransacaoPagamento.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        TransacaoPagamento transacao = pagamentoService.gerarCobrancaPix(tituloId, GatewayPagamento.MOCK);

        // Assert
        assertNotNull(transacao);
        assertEquals(GatewayPagamento.MOCK, transacao.getGateway());
        assertEquals(StatusTransacao.PENDING, transacao.getStatus());
        assertNotNull(transacao.getGatewayTransacaoId());
        assertTrue(transacao.getGatewayTransacaoId().startsWith("mock_tx_"));
        assertNotNull(transacao.getQrCodePayload());
        assertTrue(transacao.getQrCodePayload().contains("br.gov.bcb.pix"));
        
        verify(transacaoRepository, times(1)).save(transacao);
    }

    @Test
    void testProcessarWebhookPagamentoPago() {
        // Arrange
        String mockGatewayTxId = "mock_tx_123456";
        
        TituloReceber titulo = new TituloReceber();
        titulo.setId(UUID.randomUUID());
        titulo.setStatus("PENDING");

        TransacaoPagamento transacao = new TransacaoPagamento();
        transacao.setGatewayTransacaoId(mockGatewayTxId);
        transacao.setStatus(StatusTransacao.PENDING);
        transacao.setTitulo(titulo);

        when(transacaoRepository.findByGatewayTransacaoId(mockGatewayTxId)).thenReturn(Optional.of(transacao));

        String webhookPayload = "{\"event\":\"payment_updated\",\"status\":\"approved\"}";

        // Act
        pagamentoService.processarWebhook(mockGatewayTxId, StatusTransacao.PAID, webhookPayload);

        // Assert
        assertEquals(StatusTransacao.PAID, transacao.getStatus());
        assertEquals("PAID", titulo.getStatus());
        assertEquals(webhookPayload, transacao.getWebhookPayload());

        verify(tituloRepository, times(1)).save(titulo);
        verify(transacaoRepository, times(1)).save(transacao);
    }
}
