package com.erp.production.controller;

import com.erp.production.dto.CupomResponse;
import com.erp.production.service.CupomService;
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
public class CupomControllerTest {

    @Mock
    private CupomService service;

    @InjectMocks
    private CupomController controller;

    @Test
    void testListarPorOrdemProducao() {
        UUID id = UUID.randomUUID();
        List<CupomResponse> list = Collections.singletonList(new CupomResponse(null, null, null, null, null, null, null, null));
        when(service.listarPorOrdemProducao(id)).thenReturn(list);
        ResponseEntity<List<CupomResponse>> result = controller.listarPorOrdemProducao(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }
}
