package com.erp.production.service;

import com.erp.production.domain.DificuldadeTecido;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TipoTrajeto;
import com.erp.production.dto.CalculoSamInput;
import com.erp.production.dto.CalculoSamOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorCalculoSamServiceTest {

    private MotorCalculoSamService service;

    @BeforeEach
    void setUp() {
        service = new MotorCalculoSamService();
    }

    @Test
    void testCalcularOperacaoCenarioBase() {
        // Cenário Base (User Request)
        // RPM: 4000 | Pontos/cm: 4 | Comprimento: 50 | Folhas: 2 | Dificuldade: NORMAL | Trajeto: RETA | Paradas Forçadas: Nulo
        CalculoSamInput input = new CalculoSamInput(
                4000,
                new BigDecimal("4"),
                new BigDecimal("50"),
                2,
                DificuldadeTecido.NORMAL,
                TipoTrajeto.RETA,
                null
        );

        CalculoSamOutput output = service.calcularOperacao(input);

        // Output Esperado:
        // Paradas: 2
        // Tempo Máquina: 3.75s
        // Tempo Manual: 6.90s
        // Tempo Padrão Total: 12.2475s
        // SAM Minutos: 0.204125

        assertEquals(2, output.getParadasUtilizadas());
        assertEquals(new BigDecimal("3.7500"), output.getTempoMaquinaSegundos());
        assertEquals(new BigDecimal("6.9000"), output.getTempoManualSegundos());
        assertEquals(new BigDecimal("12.2475"), output.getTempoPadraoSegundos());
        assertEquals(new BigDecimal("0.204125"), output.getSamMinutos());
    }

    @Test
    void testCalcularTempoTotalPeca() {
        FichaTecnicaOperacao op1 = new FichaTecnicaOperacao();
        op1.setRpmMaquina(4000);
        op1.setPontosPorCm(new BigDecimal("4"));
        op1.setComprimentoCosturaCm(new BigDecimal("50"));
        op1.setQuantidadeFolhas(2);
        op1.setDificuldadeTecido(DificuldadeTecido.NORMAL);
        op1.setTipoTrajeto(TipoTrajeto.RETA);

        FichaTecnicaOperacao op2 = new FichaTecnicaOperacao();
        op2.setSamMinutos(new BigDecimal("0.300000")); // fallback simulation

        BigDecimal total = service.calcularTempoTotalPeca(Arrays.asList(op1, op2));

        // op1 = 0.204125, op2 = 0.300000 => total = 0.504125
        assertEquals(new BigDecimal("0.504125"), total);
        assertEquals(new BigDecimal("0.204125"), op1.getSamMinutos()); // Verifica se salvou
    }
}
