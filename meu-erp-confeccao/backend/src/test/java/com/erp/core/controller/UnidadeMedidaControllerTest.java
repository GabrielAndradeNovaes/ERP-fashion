package com.erp.core.controller;

import com.erp.core.dto.UnidadeMedidaRequest;
import com.erp.core.dto.UnidadeMedidaResponse;
import com.erp.core.service.UnidadeMedidaService;
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
public class UnidadeMedidaControllerTest {

    @Mock
    private UnidadeMedidaService service;

    @InjectMocks
    private UnidadeMedidaController controller;

    @Test
    void testCreate() {
        UnidadeMedidaRequest req = new UnidadeMedidaRequest(null, null, null, null, null, null);
        UnidadeMedidaResponse res = new UnidadeMedidaResponse(null, null, null, null, null, null, null);
        when(service.create(req)).thenReturn(res);
        ResponseEntity<UnidadeMedidaResponse> result = controller.create(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAll() {
        List<UnidadeMedidaResponse> list = Collections.singletonList(new UnidadeMedidaResponse(null, null, null, null, null, null, null));
        when(service.getAll()).thenReturn(list);
        ResponseEntity<List<UnidadeMedidaResponse>> result = controller.getAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        UnidadeMedidaResponse res = new UnidadeMedidaResponse(null, null, null, null, null, null, null);
        when(service.getById(id)).thenReturn(res);
        ResponseEntity<UnidadeMedidaResponse> result = controller.getById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        UnidadeMedidaRequest req = new UnidadeMedidaRequest(null, null, null, null, null, null);
        UnidadeMedidaResponse res = new UnidadeMedidaResponse(null, null, null, null, null, null, null);
        when(service.update(id, req)).thenReturn(res);
        ResponseEntity<UnidadeMedidaResponse> result = controller.update(id, req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> result = controller.delete(id);
        verify(service).delete(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
