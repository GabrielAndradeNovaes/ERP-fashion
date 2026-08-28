package com.erp.core.controller;

import com.erp.core.dto.ClienteRequest;
import com.erp.core.dto.ClienteResponse;
import com.erp.core.service.ClienteService;
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
public class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @InjectMocks
    private ClienteController controller;

    @Test
    void testCreate() {
        ClienteRequest req = new ClienteRequest(null, null, null, null, null, null, null, null, null, null, null);
        ClienteResponse res = new ClienteResponse(null, null, null, null, null, null, null, null, null, null, null, null);
        when(service.create(req)).thenReturn(res);
        ResponseEntity<ClienteResponse> result = controller.create(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAll() {
        List<ClienteResponse> list = Collections.singletonList(new ClienteResponse(null, null, null, null, null, null, null, null, null, null, null, null));
        when(service.getAll()).thenReturn(list);
        ResponseEntity<List<ClienteResponse>> result = controller.getAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        ClienteResponse res = new ClienteResponse(null, null, null, null, null, null, null, null, null, null, null, null);
        when(service.getById(id)).thenReturn(res);
        ResponseEntity<ClienteResponse> result = controller.getById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        ClienteRequest req = new ClienteRequest(null, null, null, null, null, null, null, null, null, null, null);
        ClienteResponse res = new ClienteResponse(null, null, null, null, null, null, null, null, null, null, null, null);
        when(service.update(id, req)).thenReturn(res);
        ResponseEntity<ClienteResponse> result = controller.update(id, req);
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
