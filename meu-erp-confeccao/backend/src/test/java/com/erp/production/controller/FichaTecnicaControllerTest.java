package com.erp.production.controller;

import com.erp.production.dto.FichaTecnicaMaterialRequest;
import com.erp.production.dto.FichaTecnicaOperacaoRequest;
import com.erp.production.dto.FichaTecnicaRequest;
import com.erp.production.dto.FichaTecnicaResponse;
import com.erp.production.service.FichaTecnicaService;
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
public class FichaTecnicaControllerTest {

    @Mock
    private FichaTecnicaService service;

    @InjectMocks
    private FichaTecnicaController controller;

    @Test
    void testCreateFichaTecnica() {
        FichaTecnicaRequest req = new FichaTecnicaRequest(null, null, null, null);
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.createFichaTecnica(req)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.createFichaTecnica(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetFichasPorProduto() {
        UUID id = UUID.randomUUID();
        List<FichaTecnicaResponse> list = Collections.singletonList(new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null));
        when(service.getFichasPorProduto(id)).thenReturn(list);
        ResponseEntity<List<FichaTecnicaResponse>> result = controller.getFichasPorProduto(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetFichaTecnicaById() {
        UUID id = UUID.randomUUID();
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.getFichaTecnicaById(id)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.getFichaTecnicaById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testAddOperacao() {
        UUID id = UUID.randomUUID();
        FichaTecnicaOperacaoRequest req = new FichaTecnicaOperacaoRequest(null, null, null, null, null, null, null);
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.addOperacao(id, req)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.addOperacao(id, req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testRemoveOperacao() {
        UUID id = UUID.randomUUID();
        UUID opId = UUID.randomUUID();
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.removeOperacao(id, opId)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.removeOperacao(id, opId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testAddMaterial() {
        UUID id = UUID.randomUUID();
        FichaTecnicaMaterialRequest req = new FichaTecnicaMaterialRequest(null, null);
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.addMaterial(id, req)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.addMaterial(id, req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testRemoveMaterial() {
        UUID id = UUID.randomUUID();
        UUID matId = UUID.randomUUID();
        FichaTecnicaResponse res = new FichaTecnicaResponse(null, null, null, null, null, null, null, null, null);
        when(service.removeMaterial(id, matId)).thenReturn(res);
        ResponseEntity<FichaTecnicaResponse> result = controller.removeMaterial(id, matId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }
}
