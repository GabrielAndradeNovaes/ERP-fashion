package com.erp.inventory.controller;

import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.dto.ProdutoMovimentacaoRequest;
import com.erp.inventory.service.EstoqueProdutoMovimentacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueProdutoControllerTest {

    @Mock
    private EstoqueProdutoMovimentacaoService service;

    @InjectMocks
    private EstoqueProdutoController controller;

    @Test
    void testRegistrarMovimentacao() {
        UUID id = UUID.randomUUID();
        ProdutoMovimentacaoRequest req = new ProdutoMovimentacaoRequest();
        req.setTipo(TipoMovimentacao.ENTRADA);
        req.setQuantidade(10);
        req.setDocumentoReferencia("DOC-123");

        EstoqueProdutoMovimentacao mov = new EstoqueProdutoMovimentacao();
        when(service.registrarMovimentacao(id, req.getTipo(), req.getQuantidade(), req.getDocumentoReferencia())).thenReturn(mov);

        ResponseEntity<EstoqueProdutoMovimentacao> result = controller.registrarMovimentacao(id, req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(mov, result.getBody());
    }

    @Test
    void testListarHistorico() {
        UUID id = UUID.randomUUID();
        List<EstoqueProdutoMovimentacao> list = Collections.singletonList(new EstoqueProdutoMovimentacao());
        when(service.listarHistoricoPorSku(id)).thenReturn(list);
        ResponseEntity<List<EstoqueProdutoMovimentacao>> result = controller.listarHistorico(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }
}
