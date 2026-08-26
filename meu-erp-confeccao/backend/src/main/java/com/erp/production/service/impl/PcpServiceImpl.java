package com.erp.production.service.impl;

import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.production.domain.Apontamento;
import com.erp.production.domain.Cupom;
import com.erp.production.domain.Ocorrencia;
import com.erp.production.dto.ApontamentoRequest;
import com.erp.production.dto.OcorrenciaRequest;
import com.erp.production.dto.ProdutividadeResponse;
import com.erp.production.repository.ApontamentoRepository;
import com.erp.production.repository.CupomRepository;
import com.erp.production.repository.OcorrenciaRepository;
import com.erp.production.service.PcpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PcpServiceImpl implements PcpService {

    private final CupomRepository cupomRepository;
    private final ApontamentoRepository apontamentoRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public PcpServiceImpl(CupomRepository cupomRepository,
                          ApontamentoRepository apontamentoRepository,
                          OcorrenciaRepository ocorrenciaRepository,
                          FuncionarioRepository funcionarioRepository) {
        this.cupomRepository = cupomRepository;
        this.apontamentoRepository = apontamentoRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    @Transactional
    public void biparCupom(ApontamentoRequest request) {
        Cupom cupom = cupomRepository.findByCodigoBarras(request.codigoBarras())
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado: " + request.codigoBarras()));

        if (cupom.getStatus() == Cupom.Status.PROCESSADO) {
            throw new IllegalStateException("ALERTA DE DUPLICIDADE: Este cupom já foi bipado anteriormente.");
        }

        Funcionario funcionario = funcionarioRepository.findById(request.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        Apontamento apontamento = new Apontamento();
        apontamento.setCupom(cupom);
        apontamento.setFuncionario(funcionario);
        
        apontamentoRepository.save(apontamento);

        cupom.setStatus(Cupom.Status.PROCESSADO);
        cupomRepository.save(cupom);
    }

    @Override
    @Transactional
    public void registrarOcorrencia(OcorrenciaRequest request) {
        Funcionario funcionario = funcionarioRepository.findById(request.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setFuncionario(funcionario);
        ocorrencia.setMotivo(request.motivo());
        ocorrencia.setTempoDescontadoCentesimal(request.tempoDescontadoCentesimal());
        
        ocorrenciaRepository.save(ocorrencia);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutividadeResponse calcularProdutividade(UUID funcionarioId, int ano, int mes) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        LocalDateTime startDate = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        List<Apontamento> apontamentos = apontamentoRepository.findByFuncionarioAndPeriod(funcionarioId, startDate, endDate);
        List<Ocorrencia> ocorrencias = ocorrenciaRepository.findByFuncionarioAndPeriod(funcionarioId, startDate, endDate);

        BigDecimal tempoProduzido = apontamentos.stream()
                .map(a -> a.getCupom().getTempoTotalCentesimal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tempoOcorrencia = ocorrencias.stream()
                .map(Ocorrencia::getTempoDescontadoCentesimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cargaHorariaMensal = funcionario.getCargaHorariaMensalPadrao();
        BigDecimal baseCalculo = cargaHorariaMensal.subtract(tempoOcorrencia);

        BigDecimal eficiencia = BigDecimal.ZERO;
        if (baseCalculo.compareTo(BigDecimal.ZERO) > 0) {
            eficiencia = tempoProduzido.divide(baseCalculo, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
        }

        return new ProdutividadeResponse(
                funcionarioId,
                funcionario.getNome(),
                mes,
                ano,
                tempoProduzido,
                cargaHorariaMensal,
                tempoOcorrencia,
                eficiencia
        );
    }
}
