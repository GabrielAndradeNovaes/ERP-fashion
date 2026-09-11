package com.erp.finance.controller;

import com.erp.finance.domain.TituloReceber;
import com.erp.finance.domain.TransacaoPagamento;
import com.erp.finance.domain.enums.GatewayPagamento;
import com.erp.finance.dto.TituloReceberDTO;
import com.erp.finance.repository.TituloReceberRepository;
import com.erp.finance.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financeiro/receber")
public class TituloReceberController {

    private final TituloReceberRepository tituloReceberRepository;
    private final PagamentoService pagamentoService;

    public TituloReceberController(TituloReceberRepository tituloReceberRepository, PagamentoService pagamentoService) {
        this.tituloReceberRepository = tituloReceberRepository;
        this.pagamentoService = pagamentoService;
    }

    private UUID getEmpresaId() {
        if (com.erp.core.tenant.EmpresaContext.getEmpresas().isEmpty()) {
            return null; // ou lançar exceção
        }
        return com.erp.core.tenant.EmpresaContext.getEmpresas().get(0);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('FINANCEIRO')")
    public ResponseEntity<List<TituloReceberDTO>> listarTitulos() {
        UUID empresaId = getEmpresaId();
        List<TituloReceberDTO> titulos = tituloReceberRepository.findByEmpresaId(empresaId).stream()
                .map(t -> new TituloReceberDTO(t.getId(), t.getDescricao(), t.getValor(), t.getDataVencimento(), t.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(titulos);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('FINANCEIRO')")
    public ResponseEntity<TituloReceberDTO> criarTitulo(@RequestBody TituloReceberDTO dto) {
        UUID empresaId = getEmpresaId();
        TituloReceber titulo = new TituloReceber();
        titulo.setEmpresaId(empresaId);
        titulo.setDescricao(dto.getDescricao());
        titulo.setValor(dto.getValor());
        titulo.setDataVencimento(dto.getDataVencimento() != null ? dto.getDataVencimento() : LocalDate.now().plusDays(7));
        titulo.setStatus("PENDING");

        titulo = tituloReceberRepository.save(titulo);
        return ResponseEntity.ok(new TituloReceberDTO(titulo.getId(), titulo.getDescricao(), titulo.getValor(), titulo.getDataVencimento(), titulo.getStatus()));
    }

    @PostMapping("/{id}/gerar-pagamento")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('FINANCEIRO')")
    public ResponseEntity<String> gerarLinkPagamento(@PathVariable UUID id, @RequestParam(defaultValue = "MOCK") String gateway) {
        TransacaoPagamento transacao = pagamentoService.gerarCobrancaPix(id, GatewayPagamento.valueOf(gateway));
        // O link para o cliente será /pagamento/{id} que o front hospedará
        return ResponseEntity.ok("/pagamento/" + id);
    }
}
