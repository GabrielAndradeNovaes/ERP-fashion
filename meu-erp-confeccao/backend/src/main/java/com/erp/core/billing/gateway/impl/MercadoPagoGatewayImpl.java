package com.erp.core.billing.gateway.impl;

import com.erp.core.billing.domain.enums.GatewayPagamento;
import com.erp.core.billing.gateway.PaymentGateway;
import com.erp.core.billing.gateway.dto.PaymentRequest;
import com.erp.core.billing.gateway.dto.PaymentResponse;
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
