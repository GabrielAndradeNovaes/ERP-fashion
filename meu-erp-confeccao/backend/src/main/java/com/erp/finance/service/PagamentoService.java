package com.erp.finance.service;

import com.erp.finance.domain.TituloReceber;
import com.erp.finance.domain.TransacaoPagamento;
import com.erp.finance.domain.enums.GatewayPagamento;
import com.erp.finance.domain.enums.MetodoPagamento;
import com.erp.finance.domain.enums.StatusTransacao;
import com.erp.finance.gateway.PaymentGateway;
import com.erp.finance.gateway.dto.PaymentRequest;
import com.erp.finance.gateway.dto.PaymentResponse;
import com.erp.finance.repository.TituloReceberRepository;
import com.erp.finance.repository.TransacaoPagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    private final TituloReceberRepository tituloRepository;
    private final TransacaoPagamentoRepository transacaoRepository;
    private final Map<String, PaymentGateway> gateways;

    public PagamentoService(TituloReceberRepository tituloRepository,
                            TransacaoPagamentoRepository transacaoRepository,
                            List<PaymentGateway> gatewayList) {
        this.tituloRepository = tituloRepository;
        this.transacaoRepository = transacaoRepository;
        // Mapeia as implementações de gateway pelo seu ID
        this.gateways = gatewayList.stream()
                .collect(Collectors.toMap(PaymentGateway::getGatewayId, Function.identity()));
    }

    @Transactional
    public TransacaoPagamento gerarCobrancaPix(UUID tituloId, GatewayPagamento gatewayTarget) {
        TituloReceber titulo = tituloRepository.findById(tituloId)
                .orElseThrow(() -> new IllegalArgumentException("Título não encontrado"));

        if (!titulo.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Título não está pendente para pagamento.");
        }

        PaymentGateway gateway = gateways.get(gatewayTarget.name());
        if (gateway == null) {
            throw new IllegalArgumentException("Gateway de pagamento não suportado ou configurado: " + gatewayTarget);
        }

        PaymentRequest request = new PaymentRequest(
                titulo.getId().toString(),
                titulo.getValor(),
                "Cliente Exemplo", // Deveria buscar do cliente real
                "cliente@exemplo.com",
                "00000000000",
                MetodoPagamento.PIX,
                titulo.getDescricao()
        );

        PaymentResponse response = gateway.createCharge(request);

        TransacaoPagamento transacao = new TransacaoPagamento();
        transacao.setTitulo(titulo);
        transacao.setGateway(gatewayTarget);
        transacao.setGatewayTransacaoId(response.getGatewayTransacaoId());
        transacao.setStatus(response.getStatus());
        transacao.setMetodoPagamento(MetodoPagamento.PIX);
        transacao.setQrCodePayload(response.getQrCodePayload());
        transacao.setQrCodeImageUrl(response.getQrCodeImageUrl());

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public void processarWebhook(String gatewayTransacaoId, StatusTransacao novoStatus, String payloadJson) {
        TransacaoPagamento transacao = transacaoRepository.findByGatewayTransacaoId(gatewayTransacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada pelo ID do gateway: " + gatewayTransacaoId));

        transacao.setStatus(novoStatus);
        transacao.setWebhookPayload(payloadJson);

        if (novoStatus == StatusTransacao.PAID) {
            TituloReceber titulo = transacao.getTitulo();
            titulo.setStatus("PAID");
            tituloRepository.save(titulo);
        }

        transacaoRepository.save(transacao);
    }
}
