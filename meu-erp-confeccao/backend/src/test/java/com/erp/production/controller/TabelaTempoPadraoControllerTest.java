package com.erp.production.controller;

import com.erp.production.dto.TabelaTempoPadraoRequest;
import com.erp.production.dto.TabelaTempoPadraoResponse;
import com.erp.production.service.TabelaTempoPadraoService;
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
public class TabelaTempoPadraoControllerTest {

    @Mock
    private TabelaTempoPadraoService service;

    @InjectMocks
    private TabelaTempoPadraoController controller;

    @Test
    void testCreate() {
        TabelaTempoPadraoRequest req = new TabelaTempoPadraoRequest(null, null, null, null);
        TabelaTempoPadraoResponse res = new TabelaTempoPadraoResponse(null, null, null, null, null);
        when(service.create(req)).thenReturn(res);
        ResponseEntity<TabelaTempoPadraoResponse> result = controller.create(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        TabelaTempoPadraoRequest req = new TabelaTempoPadraoRequest(null, null, null, null);
        TabelaTempoPadraoResponse res = new TabelaTempoPadraoResponse(null, null, null, null, null);
        when(service.update(id, req)).thenReturn(res);
        ResponseEntity<TabelaTempoPadraoResponse> result = controller.update(id, req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAll() {
        List<TabelaTempoPadraoResponse> list = Collections.singletonList(new TabelaTempoPadraoResponse(null, null, null, null, null));
        when(service.getAll()).thenReturn(list);
        ResponseEntity<List<TabelaTempoPadraoResponse>> result = controller.getAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> result = controller.delete(id);
        verify(service).delete(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
