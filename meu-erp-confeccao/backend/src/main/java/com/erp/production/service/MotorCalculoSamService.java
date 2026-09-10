package com.erp.production.service;

import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TipoTrajeto;
import com.erp.production.dto.CalculoSamInput;
import com.erp.production.dto.CalculoSamOutput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MotorCalculoSamService {

    private static final double TEMPO_PARADA_ALINHAMENTO = 1.2;
    private static final double TEMPO_DESCARTE_PECA = 2.0;
    private static final double EFICIENCIA_MAQUINA = 0.80;
    private static final double FATOR_TOLERANCIA_PFD = 1.15;

    public CalculoSamOutput calcularOperacao(CalculoSamInput input) {
        // Validações básicas
        if (input.getRpmMaquina() == null || input.getRpmMaquina() <= 0) {
            throw new IllegalArgumentException("RPM da máquina inválido.");
        }
        if (input.getPontosPorCm() == null || input.getPontosPorCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Pontos por CM / Pontos Totais inválido.");
        }
        
        boolean isCicloFixo = input.getTipoTrajeto() == TipoTrajeto.CICLO_FIXO;
        
        if (!isCicloFixo && (input.getComprimentoCosturaCm() == null || input.getComprimentoCosturaCm().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Comprimento de costura inválido para trajeto contínuo.");
        }

        double comprimento = input.getComprimentoCosturaCm() != null ? input.getComprimentoCosturaCm().doubleValue() : 0.0;

        // 3. Regra de Negócio: Proporcionalidade de Paradas
        int paradasCalculadas;
        if (input.getParadasForcadas() != null) {
            paradasCalculadas = input.getParadasForcadas();
        } else {
            if (isCicloFixo) {
                paradasCalculadas = 1; // Máquinas de ciclo fixo costumam ter 1 parada por ciclo de peça
            } else if (input.getTipoTrajeto() == TipoTrajeto.RETA) {
                paradasCalculadas = (int) Math.floor(comprimento / 25.0);
            } else { // CURVA
                paradasCalculadas = (int) Math.floor(comprimento / 12.0);
            }
        }

        // 4. Etapa A: Tempo de Máquina (Exato)
        double totalPontos;
        if (isCicloFixo) {
            totalPontos = input.getPontosPorCm().doubleValue(); // Aproveitando o campo para pontos totais
        } else {
            totalPontos = comprimento * input.getPontosPorCm().doubleValue();
        }
        
        double rpmEfetiva = input.getRpmMaquina() * EFICIENCIA_MAQUINA;
        double tempoMaquinaSegundos = (totalPontos / rpmEfetiva) * 60.0;

        // 4. Etapa B: Tempo Manual (Humano)
        double tempoBaseFolhas = getTempoBaseFolhas(input.getQuantidadeFolhas());
        double tempoTotalParadas = paradasCalculadas * TEMPO_PARADA_ALINHAMENTO;
        double tempoManualBruto = tempoBaseFolhas + tempoTotalParadas + TEMPO_DESCARTE_PECA;
        
        double multiplicadorDificuldade = input.getDificuldadeTecido() != null ? input.getDificuldadeTecido().getMultiplicador() : 1.0;
        double tempoManualSegundos = tempoManualBruto * multiplicadorDificuldade;

        // 4. Etapa C: Finalização (Ciclo e SAM)
        double tempoCicloNormal = tempoMaquinaSegundos + tempoManualSegundos;
        double tempoPadraoSegundos = tempoCicloNormal * FATOR_TOLERANCIA_PFD;
        double samMinutos = tempoPadraoSegundos / 60.0;

        // Output
        CalculoSamOutput output = new CalculoSamOutput();
        output.setParadasUtilizadas(paradasCalculadas);
        output.setTempoMaquinaSegundos(BigDecimal.valueOf(tempoMaquinaSegundos).setScale(4, RoundingMode.HALF_UP));
        output.setTempoManualSegundos(BigDecimal.valueOf(tempoManualSegundos).setScale(4, RoundingMode.HALF_UP));
        output.setTempoPadraoSegundos(BigDecimal.valueOf(tempoPadraoSegundos).setScale(4, RoundingMode.HALF_UP));
        output.setSamMinutos(BigDecimal.valueOf(samMinutos).setScale(6, RoundingMode.HALF_UP));

        return output;
    }

    private double getTempoBaseFolhas(Integer quantidadeFolhas) {
        if (quantidadeFolhas == null) return 1.8;
        switch (quantidadeFolhas) {
            case 1: return 1.8;
            case 2: return 2.5;
            case 3: return 3.5;
            case 4: return 4.8;
            default: return 1.8 + (quantidadeFolhas - 1) * 0.7; // extrapolando caso seja maior que 4, opcional
        }
    }

    public BigDecimal calcularTempoTotalPeca(List<FichaTecnicaOperacao> operacoes) {
        if (operacoes == null || operacoes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal samTotalPeca = BigDecimal.ZERO;

        for (FichaTecnicaOperacao op : operacoes) {
            if (op.getRpmMaquina() != null && op.getPontosPorCm() != null && (op.getTipoTrajeto() == TipoTrajeto.CICLO_FIXO || op.getComprimentoCosturaCm() != null)) {
                CalculoSamInput input = new CalculoSamInput();
                input.setRpmMaquina(op.getRpmMaquina());
                input.setPontosPorCm(op.getPontosPorCm());
                input.setComprimentoCosturaCm(op.getComprimentoCosturaCm() != null ? op.getComprimentoCosturaCm() : BigDecimal.ZERO);
                input.setQuantidadeFolhas(op.getQuantidadeFolhas());
                input.setDificuldadeTecido(op.getDificuldadeTecido());
                input.setTipoTrajeto(op.getTipoTrajeto());
                input.setParadasForcadas(op.getQuantidadeParadas()); // Se houver paradas forçadas

                CalculoSamOutput output = calcularOperacao(input);
                
                // Opcional: Atualizar a própria operação com o resultado
                op.setSamMinutos(output.getSamMinutos());
                op.setQuantidadeParadas(output.getParadasUtilizadas()); // Salva as paradas que foram calculadas

                samTotalPeca = samTotalPeca.add(output.getSamMinutos());
            } else if (op.getSamMinutos() != null) {
                // Caso a operação já tenha o SAM e não tenha os dados detalhados
                samTotalPeca = samTotalPeca.add(op.getSamMinutos());
            } else if (op.getTempoCalculadoCentesimal() != null) {
                // Fallback para o tempo antigo (se houver necessidade)
                samTotalPeca = samTotalPeca.add(op.getTempoCalculadoCentesimal().multiply(BigDecimal.valueOf(0.6))); // Convertendo de hora centesimal para minuto? Não, 1 hora = 60 min. Então 1 centesimal = 0.6 min.
            }
        }

        return samTotalPeca.setScale(6, RoundingMode.HALF_UP);
    }
}
