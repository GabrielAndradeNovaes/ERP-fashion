package com.erp.core.billing.gateway.impl;

import com.erp.core.billing.domain.enums.GatewayPagamento;
import com.erp.core.billing.domain.enums.MetodoPagamento;
import com.erp.core.billing.domain.enums.StatusTransacao;
import com.erp.core.billing.gateway.PaymentGateway;
import com.erp.core.billing.gateway.dto.PaymentRequest;
import com.erp.core.billing.gateway.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentGatewayImpl implements PaymentGateway {

    @Override
    public String getGatewayId() {
        return GatewayPagamento.MOCK.name();
    }

    @Override
    public PaymentResponse createCharge(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        // Simulando a criação de um ID de transação no gateway externo
        String mockGatewayId = "mock_tx_" + UUID.randomUUID().toString().substring(0, 8);
        response.setGatewayTransacaoId(mockGatewayId);
        response.setStatus(StatusTransacao.PENDING);
        
        if (request.getMetodo() == MetodoPagamento.PIX) {
            response.setQrCodePayload("00020101021126580014br.gov.bcb.pix0136mock-pix-key-for-" + request.getTituloId());
            response.setQrCodeImageUrl("https://api.mockgateway.com/qrcode/" + mockGatewayId + ".png");
        } else if (request.getMetodo() == MetodoPagamento.BOLETO) {
            response.setBoletoUrl("https://api.mockgateway.com/boleto/" + mockGatewayId + ".pdf");
            response.setBoletoLinhaDigitavel("34191.09008 00000.000004 00000.000000 1 00000000000000");
        }

        return response;
    }

    @Override
    public PaymentResponse checkStatus(String gatewayTransacaoId) {
        // No mock, vamos apenas simular que sempre está pendente
        return new PaymentResponse(gatewayTransacaoId, StatusTransacao.PENDING);
    }
}
