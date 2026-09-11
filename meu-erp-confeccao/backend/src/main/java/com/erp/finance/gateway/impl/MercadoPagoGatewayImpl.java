package com.erp.finance.gateway.impl;

import com.erp.finance.domain.enums.GatewayPagamento;
import com.erp.finance.gateway.PaymentGateway;
import com.erp.finance.gateway.dto.PaymentRequest;
import com.erp.finance.gateway.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class MercadoPagoGatewayImpl implements PaymentGateway {

    @Override
    public String getGatewayId() {
        return GatewayPagamento.MERCADO_PAGO.name();
    }

    @Override
    public PaymentResponse createCharge(PaymentRequest request) {
        // TODO: Integrar com a SDK do Mercado Pago no futuro
        throw new UnsupportedOperationException("Integração com Mercado Pago ainda não implementada.");
    }

    @Override
    public PaymentResponse checkStatus(String gatewayTransacaoId) {
        // TODO: Integrar com a SDK do Mercado Pago no futuro
        throw new UnsupportedOperationException("Integração com Mercado Pago ainda não implementada.");
    }
}
