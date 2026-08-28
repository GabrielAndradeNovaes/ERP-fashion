package com.erp.core.service;

import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.core.dto.DashboardResumoDTO;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.repository.OrdemProducaoRepository;
import org.springframework.stereotype.Service;


@Service
public class DashboardService {

    private final ProdutoBaseRepository produtoBaseRepository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final MaterialRepository materialRepository;

    public DashboardService(ProdutoBaseRepository produtoBaseRepository,
                            OrdemProducaoRepository ordemProducaoRepository,
                            MaterialRepository materialRepository) {
        this.produtoBaseRepository = produtoBaseRepository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.materialRepository = materialRepository;
    }

    public DashboardResumoDTO getResumo() {
        long totalProdutos = produtoBaseRepository.count();
        long opsEmAndamento = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.EM_ANDAMENTO)
                            + ordemProducaoRepository.countByStatus(OrdemProducaoStatus.FACCAO);
        long opsConcluidas = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.CONCLUIDA);
        
        // Calcular valor total de estoque (soma de quantidade * custo)
        Double valorEstoque = materialRepository.findAll().stream()
                .mapToDouble(m -> m.getQuantidadeAtual().doubleValue() * m.getCustoUnitario().doubleValue())
                .sum();

        return new DashboardResumoDTO(totalProdutos, opsEmAndamento, opsConcluidas, valorEstoque);
    }
}
