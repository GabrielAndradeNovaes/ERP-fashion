package com.erp.core.billing.controller;

import com.erp.core.billing.domain.FaturaSaaS;
import com.erp.core.billing.domain.TransacaoFatura;
import com.erp.core.billing.domain.enums.StatusTransacao;
import com.erp.core.billing.dto.CheckoutResponseDTO;
import com.erp.core.billing.repository.FaturaSaaSRepository;
import com.erp.core.billing.service.FaturamentoSaaSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/checkout-saas")
public class CheckoutSaaSController {

    private final FaturaSaaSRepository faturaSaaSRepository;
    private final FaturamentoSaaSService faturamentoSaaSService;

    public CheckoutSaaSController(FaturaSaaSRepository faturaSaaSRepository, FaturamentoSaaSService faturamentoSaaSService) {
        this.faturaSaaSRepository = faturaSaaSRepository;
        this.faturamentoSaaSService = faturamentoSaaSService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckoutResponseDTO> obterDetalhesPagamento(@PathVariable UUID id) {
        Optional<FaturaSaaS> faturaOpt = faturaSaaSRepository.findById(id);
        if (faturaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FaturaSaaS fatura = faturaOpt.get();
        String qrCodePayload = null;
        String gatewayTransacaoId = null;

        // Pegar a última transação PENDENTE
        Optional<TransacaoFatura> transacaoOpt = fatura.getTransacoes().stream()
                .filter(t -> t.getStatus() == StatusTransacao.PENDING)
                .reduce((first, second) -> second); // pega o último
                
        if (transacaoOpt.isPresent()) {
            qrCodePayload = transacaoOpt.get().getQrCodePayload();
            gatewayTransacaoId = transacaoOpt.get().getGatewayTransacaoId();
        }

        CheckoutResponseDTO dto = new CheckoutResponseDTO(
                fatura.getId(),
                "ERP Confecção (SaaS)",
                fatura.getDescricao(),
                fatura.getValor(),
                fatura.getStatus(),
                qrCodePayload,
                gatewayTransacaoId
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/webhook/mock")
    public ResponseEntity<String> mockWebhook(@RequestBody Map<String, Object> payload) {
        String gatewayTransacaoId = (String) payload.get("gatewayTransacaoId");
        String statusStr = (String) payload.get("status");

        faturamentoSaaSService.processarWebhook(gatewayTransacaoId, StatusTransacao.valueOf(statusStr), payload.toString());
        
        return ResponseEntity.ok("Webhook mock processado (SaaS).");
    }
}
