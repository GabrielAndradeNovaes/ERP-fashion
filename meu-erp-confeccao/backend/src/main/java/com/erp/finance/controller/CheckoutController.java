package com.erp.finance.controller;

import com.erp.finance.domain.TituloReceber;
import com.erp.finance.domain.TransacaoPagamento;
import com.erp.finance.domain.enums.StatusTransacao;
import com.erp.finance.dto.CheckoutResponseDTO;
import com.erp.finance.repository.TituloReceberRepository;
import com.erp.finance.repository.TransacaoPagamentoRepository;
import com.erp.finance.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/public/checkout")
public class CheckoutController {

    private final TituloReceberRepository tituloRepository;
    private final PagamentoService pagamentoService;

    public CheckoutController(TituloReceberRepository tituloRepository, PagamentoService pagamentoService) {
        this.tituloRepository = tituloRepository;
        this.pagamentoService = pagamentoService;
    }

    @GetMapping("/{tituloId}")
    public ResponseEntity<CheckoutResponseDTO> obterDetalhesPagamento(@PathVariable UUID tituloId) {
        TituloReceber titulo = tituloRepository.findById(tituloId).orElse(null);
        if (titulo == null) {
            return ResponseEntity.notFound().build();
        }

        List<TransacaoPagamento> transacoes = titulo.getTransacoes();
        TransacaoPagamento transacaoRecente = transacoes.stream()
                .max(Comparator.comparing(TransacaoPagamento::getCriadoEm))
                .orElse(null);

        CheckoutResponseDTO response = new CheckoutResponseDTO(
                titulo.getDescricao(),
                titulo.getValor(),
                titulo.getStatus(),
                transacaoRecente != null ? transacaoRecente.getQrCodePayload() : null,
                transacaoRecente != null ? transacaoRecente.getQrCodeImageUrl() : null,
                transacaoRecente != null ? transacaoRecente.getStatus() : null
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint simulado de webhook que a interface usará apenas para testes de desenvolvimento,
    // Em produção, isso seria chamado pela Asaas/MercadoPago, mas faremos a tela do cliente chamar pra simular que ele pagou
    @PostMapping("/mock-webhook/{tituloId}")
    public ResponseEntity<Void> mockSimularPagamento(@PathVariable UUID tituloId) {
        TituloReceber titulo = tituloRepository.findById(tituloId).orElseThrow();
        TransacaoPagamento transacaoRecente = titulo.getTransacoes().stream()
                .max(Comparator.comparing(TransacaoPagamento::getCriadoEm))
                .orElseThrow();
                
        pagamentoService.processarWebhook(transacaoRecente.getGatewayTransacaoId(), StatusTransacao.PAID, "{\"simulado\": true}");
        
        return ResponseEntity.ok().build();
    }
}
