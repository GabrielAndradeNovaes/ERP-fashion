package com.erp.production.controller;

import com.erp.production.dto.ApontamentoRequest;
import com.erp.production.dto.OcorrenciaRequest;
import com.erp.production.dto.ProdutividadeResponse;
import com.erp.production.service.PcpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BipagemControllerTest {

    @Mock
    private PcpService pcpService;

    @InjectMocks
    private BipagemController controller;

    @Test
    void testBiparCupom() {
        ApontamentoRequest req = new ApontamentoRequest(null, null);
        ResponseEntity<Void> result = controller.biparCupom(req);
        verify(pcpService).biparCupom(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRegistrarOcorrencia() {
        OcorrenciaRequest req = new OcorrenciaRequest(null, null, null);
        ResponseEntity<Void> result = controller.registrarOcorrencia(req);
        verify(pcpService).registrarOcorrencia(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetProdutividade() {
        UUID id = UUID.randomUUID();
        ProdutividadeResponse res = new ProdutividadeResponse(null, null, 0, 0, null, null, null, null);
        when(pcpService.calcularProdutividade(id, 2023, 1)).thenReturn(res);
        ResponseEntity<ProdutividadeResponse> result = controller.getProdutividade(id, 1, 2023);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }
}
