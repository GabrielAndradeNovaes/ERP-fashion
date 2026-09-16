package com.erp.core.service;

import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.core.dto.DashboardResumoDTO;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.repository.OrdemProducaoRepository;
import com.erp.finance.domain.TituloReceber;
import com.erp.finance.repository.TituloReceberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProdutoBaseRepository produtoBaseRepository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final MaterialRepository materialRepository;
    private final TituloReceberRepository tituloReceberRepository;

    public DashboardService(ProdutoBaseRepository produtoBaseRepository,
                            OrdemProducaoRepository ordemProducaoRepository,
                            MaterialRepository materialRepository,
                            TituloReceberRepository tituloReceberRepository) {
        this.produtoBaseRepository = produtoBaseRepository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.materialRepository = materialRepository;
        this.tituloReceberRepository = tituloReceberRepository;
    }

    public Map<String, Object> getResumo() {
        Map<String, Object> response = new HashMap<>();

        // 1. Basic Metrics
        long totalProdutos = produtoBaseRepository.count();
        long opsEmAndamento = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.EM_ANDAMENTO)
                            + ordemProducaoRepository.countByStatus(OrdemProducaoStatus.FACCAO);
        long opsConcluidas = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.CONCLUIDA);
        
        Double valorEstoque = materialRepository.findAll().stream()
                .filter(m -> m.getQuantidadeAtual() != null && m.getCustoUnitario() != null)
                .mapToDouble(m -> m.getQuantidadeAtual().doubleValue() * m.getCustoUnitario().doubleValue())
                .sum();

        DashboardResumoDTO metrics = new DashboardResumoDTO(totalProdutos, opsEmAndamento, opsConcluidas, valorEstoque);
        response.put("metrics", metrics);

        // 2. OP Status Distribution (Pie Chart)
        long opsPendentes = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.PENDENTE);
        
        List<Map<String, Object>> opStatusDistribution = Arrays.asList(
            Map.of("name", "Pendente", "value", opsPendentes, "color", "#f59e0b"),
            Map.of("name", "Em Andamento", "value", opsEmAndamento, "color", "#3b82f6"),
            Map.of("name", "Concluída", "value", opsConcluidas, "color", "#10b981")
        );
        response.put("opStatusDistribution", opStatusDistribution);

        // 3. Productivity History (Mocked until fully wired)
        List<Map<String, Object>> productivityHistory = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        int[] dummyMinutes = {450, 520, 480, 600, 590, 310, 490};
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            productivityHistory.add(Map.of(
                "name", date.format(formatter),
                "minutos", dummyMinutes[6 - i]
            ));
        }
        response.put("productivityHistory", productivityHistory);

        // 4. Upcoming Receivables (Real Data)
        List<TituloReceber> titulosReceber = tituloReceberRepository.findAll();
        List<Map<String, Object>> upcomingReceivables = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            LocalDate start = today.plusDays(i * 7);
            LocalDate end = start.plusDays(6);
            
            double receber = titulosReceber.stream()
                .filter(t -> t.getStatus() == TituloReceber.Status.PENDENTE)
                .filter(t -> !t.getDataVencimento().isBefore(start) && !t.getDataVencimento().isAfter(end))
                .map(t -> t.getValor().doubleValue())
                .reduce(0.0, Double::sum);
                
            double inadimplente = titulosReceber.stream()
                .filter(t -> t.getStatus() == TituloReceber.Status.PENDENTE)
                .filter(t -> t.getDataVencimento().isBefore(today))
                .map(t -> t.getValor().doubleValue())
                .reduce(0.0, Double::sum);
            
            // Only sum inadimplente for the first week to show on chart, or calculate it differently.
            // Actually, inadimplente is accumulated, so we just show it on week 1 for the chart
            upcomingReceivables.add(Map.of(
                "name", "Semana " + (i + 1),
                "receber", receber,
                "inadimplente", i == 0 ? inadimplente : 0.0
            ));
        }
        response.put("upcomingReceivables", upcomingReceivables);

        // 5. OPs Atrasadas
        List<OrdemProducao> topOpsAtrasadas = ordemProducaoRepository.findTop5ByStatusNotOrderByCriadoEmAsc(OrdemProducaoStatus.CONCLUIDA);
        List<Map<String, Object>> opsAtrasadas = topOpsAtrasadas.stream().map(op -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", op.getId());
            map.put("numero", op.getNumero());
            map.put("produto", op.getProdutoBase().getNome());
            map.put("criadoEm", op.getCriadoEm());
            map.put("status", op.getStatus().name());
            return map;
        }).collect(Collectors.toList());
        response.put("opsAtrasadas", opsAtrasadas);

        // 6. Estoque Crítico
        List<Material> topMateriaisCriticos = materialRepository.findTop5ByStatusOrderByQuantidadeAtualAsc("ATIVO");
        List<Map<String, Object>> estoqueCritico = topMateriaisCriticos.stream().map(m -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", m.getId());
            map.put("codigo", m.getCodigo());
            map.put("nome", m.getNome());
            map.put("quantidadeAtual", m.getQuantidadeAtual() != null ? m.getQuantidadeAtual() : BigDecimal.ZERO);
            map.put("unidade", m.getUnidadeMedida() != null ? m.getUnidadeMedida().getSigla() : "");
            return map;
        }).collect(Collectors.toList());
        response.put("estoqueCritico", estoqueCritico);

        return response;
    }
}
