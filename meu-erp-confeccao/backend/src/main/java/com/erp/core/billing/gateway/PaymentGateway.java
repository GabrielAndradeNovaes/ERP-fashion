package com.erp.core.billing.gateway;

import com.erp.core.billing.gateway.dto.PaymentRequest;
import com.erp.core.billing.gateway.dto.PaymentResponse;

public interface PaymentGateway {
    
    /**
     * Identificador do Gateway (ex: MOCK, MERCADO_PAGO)
     */
    String getGatewayId();

    /**
     * Cria uma nova cobrança no gateway externo
     */
    PaymentResponse createCharge(PaymentRequest request);

    /**
     * Verifica o status de uma cobrança existente
     */
    PaymentResponse checkStatus(String gatewayTransacaoId);
}
