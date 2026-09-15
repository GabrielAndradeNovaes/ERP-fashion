package com.erp.core.service;

import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.core.dto.DashboardResumoDTO;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.repository.OrdemProducaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public Map<String, Object> getResumo() {
        Map<String, Object> response = new HashMap<>();

        // 1. Basic Metrics
        long totalProdutos = produtoBaseRepository.count();
        long opsEmAndamento = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.EM_ANDAMENTO)
                            + ordemProducaoRepository.countByStatus(OrdemProducaoStatus.FACCAO);
        long opsConcluidas = ordemProducaoRepository.countByStatus(OrdemProducaoStatus.CONCLUIDA);
        
        Double valorEstoque = materialRepository.findAll().stream()
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

        // 3. Productivity History (Last 7 days) - Mocked Seed Data since Apontamento isn't fully wired for this yet
        List<Map<String, Object>> productivityHistory = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Generating some realistic looking dummy data for the last 7 days
        int[] dummyMinutes = {450, 520, 480, 600, 590, 310, 490};
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            productivityHistory.add(Map.of(
                "name", date.format(formatter),
                "minutos", dummyMinutes[6 - i]
            ));
        }
        response.put("productivityHistory", productivityHistory);

        // 4. Upcoming Receivables (Mocked Seed Data)
        List<Map<String, Object>> upcomingReceivables = Arrays.asList(
            Map.of("name", "Semana 1", "receber", 12500.00, "inadimplente", 1200.00),
            Map.of("name", "Semana 2", "receber", 8400.00, "inadimplente", 0.0),
            Map.of("name", "Semana 3", "receber", 15200.00, "inadimplente", 3500.00),
            Map.of("name", "Semana 4", "receber", 9800.00, "inadimplente", 800.00)
        );
        response.put("upcomingReceivables", upcomingReceivables);

        return response;
    }
}
