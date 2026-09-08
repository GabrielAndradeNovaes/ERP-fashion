package com.erp.production.service;

import com.erp.production.repository.ApontamentoRepository;
import com.erp.production.domain.Apontamento;
import com.erp.production.dto.ProdutividadeResumo;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.core.domain.Funcionario;
import com.erp.finance.service.FinanceiroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutividadeService {

    private final ApontamentoRepository apontamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FinanceiroService financeiroService;

    public ProdutividadeService(ApontamentoRepository apontamentoRepository, FuncionarioRepository funcionarioRepository, FinanceiroService financeiroService) {
        this.apontamentoRepository = apontamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.financeiroService = financeiroService;
    }

    public List<ProdutividadeResumo> getResumo(LocalDateTime start, LocalDateTime end) {
        List<Funcionario> costureiras = funcionarioRepository.findByAtivoTrue();
        List<ProdutividadeResumo> comApontamentos = apontamentoRepository.getProdutividadeResumo(start, end);
        
        java.util.Map<UUID, ProdutividadeResumo> mapa = new java.util.HashMap<>();
        for (ProdutividadeResumo r : comApontamentos) {
            mapa.put(r.funcionarioId(), r);
        }

        return costureiras.stream().map(f -> {
            ProdutividadeResumo r = mapa.get(f.getId());
            if (r != null) {
                return new ProdutividadeResumo(f.getId(), f.getNome(), r.totalCupons(), r.tempoPadraoProduzido() != null ? r.tempoPadraoProduzido() : BigDecimal.ZERO);
            }
            return new ProdutividadeResumo(f.getId(), f.getNome(), 0L, BigDecimal.ZERO);
        }).toList();
    }

    @Transactional
    public void pagarProdutividade(UUID funcionarioId, LocalDateTime start, LocalDateTime end, BigDecimal valorPagar) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId).orElseThrow();
        
        List<Apontamento> apontamentos = apontamentoRepository.findNaoPagosByFuncionarioAndPeriod(funcionarioId, start, end);
        if (apontamentos.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        for (Apontamento a : apontamentos) {
            a.setPago(true);
            a.setDataPagamento(now);
        }
        apontamentoRepository.saveAll(apontamentos);

        String descricao = "Pagamento de Produtividade - Período: " + start.toLocalDate() + " a " + end.toLocalDate();
        financeiroService.criarTituloPagamentoFuncionario(funcionario.getEmpresa(), funcionario, valorPagar, descricao);
    }
}
