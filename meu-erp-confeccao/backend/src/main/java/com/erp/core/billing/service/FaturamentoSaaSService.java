package com.erp.core.billing.service;

import com.erp.core.billing.domain.FaturaSaaS;
import com.erp.core.billing.domain.TransacaoFatura;
import com.erp.core.billing.domain.enums.GatewayPagamento;
import com.erp.core.billing.domain.enums.StatusTransacao;
import com.erp.core.billing.gateway.PaymentGateway;
import com.erp.core.billing.gateway.dto.PaymentRequest;
import com.erp.core.billing.gateway.dto.PaymentResponse;
import com.erp.core.billing.repository.FaturaSaaSRepository;
import com.erp.core.billing.repository.TransacaoFaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FaturamentoSaaSService {

    private final FaturaSaaSRepository faturaSaaSRepository;
    private final TransacaoFaturaRepository transacaoFaturaRepository;
    private final Map<GatewayPagamento, PaymentGateway> gateways;

    public FaturamentoSaaSService(
            FaturaSaaSRepository faturaSaaSRepository,
            TransacaoFaturaRepository transacaoFaturaRepository,
            List<PaymentGateway> gatewayList) {
        this.faturaSaaSRepository = faturaSaaSRepository;
        this.transacaoFaturaRepository = transacaoFaturaRepository;
        this.gateways = gatewayList.stream()
                .collect(Collectors.toMap(g -> GatewayPagamento.valueOf(g.getGatewayId()), g -> g));
    }

    @Transactional
    public String gerarPagamentoParaFatura(UUID faturaId, GatewayPagamento gatewaySelecionado) {
        FaturaSaaS fatura = faturaSaaSRepository.findById(faturaId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada"));

        PaymentGateway gateway = gateways.get(gatewaySelecionado);
        if (gateway == null) {
            throw new IllegalArgumentException("Gateway de pagamento não suportado: " + gatewaySelecionado);
        }

        // 1. Prepara Request (Dá pra pegar dados do tenant se necessário)
        PaymentRequest request = new PaymentRequest();
        request.setTituloId(fatura.getId().toString());
        request.setValor(fatura.getValor());
        request.setDescricao(fatura.getDescricao());
        request.setNomeCliente(fatura.getTenant().getNomeEmpresa());
        request.setEmailCliente("admin@" + fatura.getTenant().getSlug() + ".com"); // mock

        // 2. Chama Gateway
        PaymentResponse response = gateway.createCharge(request);

        // 3. Salva Transação
        TransacaoFatura transacao = new TransacaoFatura();
        transacao.setGateway(gatewaySelecionado);
        transacao.setGatewayTransacaoId(response.getGatewayTransacaoId());
        transacao.setStatus(response.getStatus());
        transacao.setQrCodePayload(response.getQrCodePayload());
        transacao.setQrCodeImageUrl(response.getQrCodeImageUrl());
        
        fatura.addTransacao(transacao);
        
        faturaSaaSRepository.save(fatura);

        return response.getQrCodePayload();
    }

    @Transactional
    public void processarWebhook(String gatewayTransacaoId, StatusTransacao novoStatus, String payloadBruto) {
        TransacaoFatura transacao = transacaoFaturaRepository.findByGatewayTransacaoId(gatewayTransacaoId);
        if (transacao == null) return;

        transacao.setStatus(novoStatus);
        transacao.setPayloadResposta(payloadBruto);

        // Atualizar Fatura se a transação foi Paga
        if (novoStatus == StatusTransacao.PAID) {
            FaturaSaaS fatura = transacao.getFaturaSaaS();
            fatura.setStatus("PAID");
            faturaSaaSRepository.save(fatura);
        } else {
            transacaoFaturaRepository.save(transacao);
        }
    }
}
