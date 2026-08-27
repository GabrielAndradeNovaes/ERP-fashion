package com.erp.core.controller;

import com.erp.core.dto.CategoriaRequest;
import com.erp.core.dto.CategoriaResponse;
import com.erp.core.service.CategoriaService;
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
public class CategoriaControllerTest {

    @Mock
    private CategoriaService service;

    @InjectMocks
    private CategoriaController controller;

    @Test
    void testCreate() {
        CategoriaRequest req = new CategoriaRequest(null, null, null, null, null, null);
        CategoriaResponse res = new CategoriaResponse(null, null, null, null, null, null, null);
        when(service.create(req)).thenReturn(res);
        ResponseEntity<CategoriaResponse> result = controller.create(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAll() {
        List<CategoriaResponse> list = Collections.singletonList(new CategoriaResponse(null, null, null, null, null, null, null));
        when(service.getAll()).thenReturn(list);
        ResponseEntity<List<CategoriaResponse>> result = controller.getAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        CategoriaResponse res = new CategoriaResponse(null, null, null, null, null, null, null);
        when(service.getById(id)).thenReturn(res);
        ResponseEntity<CategoriaResponse> result = controller.getById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        CategoriaRequest req = new CategoriaRequest(null, null, null, null, null, null);
        CategoriaResponse res = new CategoriaResponse(null, null, null, null, null, null, null);
        when(service.update(id, req)).thenReturn(res);
        ResponseEntity<CategoriaResponse> result = controller.update(id, req);
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
