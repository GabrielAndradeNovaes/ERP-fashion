package com.erp.finance.gateway;

import com.erp.finance.gateway.dto.PaymentRequest;
import com.erp.finance.gateway.dto.PaymentResponse;

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
