package com.erp.catalog.controller;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.service.ProdutoService;
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
public class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController controller;

    @Test
    void testCreateProduto() {
        ProdutoBaseRequest req = new ProdutoBaseRequest(null, null, null, null, null, null);
        ProdutoBaseResponse res = new ProdutoBaseResponse(null, null, null, null, null, null, null, null);
        when(produtoService.createProduto(req)).thenReturn(res);
        ResponseEntity<ProdutoBaseResponse> result = controller.createProduto(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAllProdutos() {
        List<ProdutoBaseResponse> list = Collections.singletonList(new ProdutoBaseResponse(null, null, null, null, null, null, null, null));
        when(produtoService.getAllProdutos()).thenReturn(list);
        ResponseEntity<List<ProdutoBaseResponse>> result = controller.getAllProdutos();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetProduto() {
        UUID id = UUID.randomUUID();
        ProdutoBaseResponse res = new ProdutoBaseResponse(null, null, null, null, null, null, null, null);
        when(produtoService.getProduto(id)).thenReturn(res);
        ResponseEntity<ProdutoBaseResponse> result = controller.getProduto(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testUpdateProduto() {
        UUID id = UUID.randomUUID();
        ProdutoBaseRequest req = new ProdutoBaseRequest(null, null, null, null, null, null);
        ProdutoBaseResponse res = new ProdutoBaseResponse(null, null, null, null, null, null, null, null);
        when(produtoService.updateProduto(id, req)).thenReturn(res);
        ResponseEntity<ProdutoBaseResponse> result = controller.updateProduto(id, req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testDeleteProduto() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> result = controller.deleteProduto(id);
        verify(produtoService).deleteProduto(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
