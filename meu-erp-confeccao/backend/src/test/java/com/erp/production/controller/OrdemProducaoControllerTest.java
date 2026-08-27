package com.erp.production.controller;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.service.OrdemProducaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdemProducaoControllerTest {

    @Mock
    private OrdemProducaoService service;

    @InjectMocks
    private OrdemProducaoController controller;

    @Test
    void testCriar() {
        OrdemProducaoRequest req = new OrdemProducaoRequest(null, null, null, null);
        OrdemProducaoResponse res = new OrdemProducaoResponse(null, null, null, null, null, null, null, null, null, null, null);
        when(service.criarOrdemProducao(req)).thenReturn(res);
        ResponseEntity<OrdemProducaoResponse> result = controller.criar(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testListarTodas() {
        List<OrdemProducaoResponse> list = Collections.singletonList(new OrdemProducaoResponse(null, null, null, null, null, null, null, null, null, null, null));
        when(service.listarTodas()).thenReturn(list);
        ResponseEntity<List<OrdemProducaoResponse>> result = controller.listarTodas();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testIniciarProducao() {
        UUID id = UUID.randomUUID();
        OrdemProducaoResponse res = new OrdemProducaoResponse(null, null, null, null, null, null, null, null, null, null, null);
        when(service.iniciarProducao(id)).thenReturn(res);
        ResponseEntity<OrdemProducaoResponse> result = controller.iniciarProducao(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGerarPacotes() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> result = controller.gerarPacotes(id, 20);
        verify(service).gerarPacotes(id, 20);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
