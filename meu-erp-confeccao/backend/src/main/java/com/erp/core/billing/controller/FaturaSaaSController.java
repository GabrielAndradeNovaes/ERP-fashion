package com.erp.core.billing.controller;

import com.erp.core.billing.domain.FaturaSaaS;
import com.erp.core.billing.domain.enums.GatewayPagamento;
import com.erp.core.billing.dto.FaturaSaaSDTO;
import com.erp.core.billing.repository.FaturaSaaSRepository;
import com.erp.core.billing.service.FaturamentoSaaSService;
import com.erp.core.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class FaturaSaaSController {

    private final FaturaSaaSRepository faturaSaaSRepository;
    private final FaturamentoSaaSService faturamentoSaaSService;

    public FaturaSaaSController(FaturaSaaSRepository faturaSaaSRepository, FaturamentoSaaSService faturamentoSaaSService) {
        this.faturaSaaSRepository = faturaSaaSRepository;
        this.faturamentoSaaSService = faturamentoSaaSService;
    }

    @GetMapping("/faturas")
    public ResponseEntity<List<FaturaSaaSDTO>> listarFaturas() {
        // Se for SuperAdmin sem tenant selecionado, poderia listar todas.
        // Aqui listamos as faturas do tenant logado.
        String tenantIdStr = TenantContext.getCurrentTenant();
        if (tenantIdStr == null || tenantIdStr.equals(TenantContext.MASTER_TENANT)) {
            // SuperAdmin view - mock por enquanto: retorna todas
            List<FaturaSaaSDTO> todas = faturaSaaSRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(todas);
        }

        UUID tenantId = UUID.fromString(tenantIdStr);
        List<FaturaSaaSDTO> faturas = faturaSaaSRepository.findByTenantId(tenantId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(faturas);
    }

    @PostMapping("/faturas/{id}/gerar-pagamento")
    public ResponseEntity<String> gerarPagamento(@PathVariable UUID id, @RequestParam GatewayPagamento gateway) {
        String qrCodeUrl = faturamentoSaaSService.gerarPagamentoParaFatura(id, gateway);
        return ResponseEntity.ok(qrCodeUrl);
    }

    private FaturaSaaSDTO toDTO(FaturaSaaS fatura) {
        return new FaturaSaaSDTO(
                fatura.getId(),
                fatura.getTenant().getId(),
                fatura.getTenant().getNomeEmpresa(),
                fatura.getDescricao(),
                fatura.getValor(),
                fatura.getDataVencimento(),
                fatura.getStatus()
        );
    }
}
