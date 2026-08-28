package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.OrdemProducaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdemProducaoServiceImplTest {

    @Mock
    private OrdemProducaoRepository ordemProducaoRepository;
    @Mock
    private ProdutoBaseRepository produtoBaseRepository;
    @Mock
    private FichaTecnicaRepository fichaTecnicaRepository;
    @Mock
    private EstoqueMovimentacaoService estoqueMovimentacaoService;
    @Mock
    private com.erp.catalog.repository.ProdutoSkuRepository produtoSkuRepository;
    @Mock
    private com.erp.production.repository.PacoteRepository pacoteRepository;
    @Mock
    private com.erp.production.repository.CupomRepository cupomRepository;

    @InjectMocks
    private OrdemProducaoServiceImpl service;

    private OrdemProducao mockOp;
    private ProdutoBase mockProduto;
    private FichaTecnica mockFicha;
    private OrdemProducaoRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockProduto = new ProdutoBase();
        mockProduto.setId(UUID.randomUUID());
        mockProduto.setNome("Camiseta");

        com.erp.catalog.domain.ProdutoSku sku = new com.erp.catalog.domain.ProdutoSku();
        sku.setId(UUID.randomUUID());
        sku.setCor("Preto");
        sku.setTamanho("M");
        sku.setProdutoBase(mockProduto);
        
        List<com.erp.catalog.domain.ProdutoSku> skus = new ArrayList<>();
        skus.add(sku);
        mockProduto.setSkus(skus);

        mockFicha = new FichaTecnica();
        mockFicha.setId(UUID.randomUUID());
        mockFicha.setVersao("v1");
        mockFicha.setMateriais(new ArrayList<>());
        
        mockProduto.setFichaTecnica(mockFicha);

        Material mat = new Material();
        mat.setId(UUID.randomUUID());
        
        FichaTecnicaMaterial ftm = new FichaTecnicaMaterial();
        ftm.setMaterial(mat);
        ftm.setQuantidade(new BigDecimal("1.5"));
        mockFicha.addMaterial(ftm);

        mockOp = new OrdemProducao();
        mockOp.setId(UUID.randomUUID());
        mockOp.setNumero("OP-001");
        mockOp.setProdutoBase(mockProduto);
        mockOp.setFichaTecnica(mockFicha);
        mockOp.setQuantidade(10);
        mockOp.setStatus(OrdemProducaoStatus.CADASTRADA);

        mockRequest = new OrdemProducaoRequest("OP-001", mockProduto.getId(), 10, null);
    }

    @Test
    void shouldCreateOrdemProducao() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(false);
        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        OrdemProducaoResponse response = service.criarOrdemProducao(mockRequest);

        assertNotNull(response);
        assertEquals("OP-001", response.numero());
        assertEquals(OrdemProducaoStatus.CADASTRADA, response.status());
    }

    @Test
    void shouldThrowExceptionIfOpNumberExists() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.criarOrdemProducao(mockRequest));
        assertTrue(ex.getMessage().contains("OP-001"));
    }

    @Test
    void shouldListAllOps() {
        when(ordemProducaoRepository.findAll()).thenReturn(List.of(mockOp));
        List<OrdemProducaoResponse> list = service.listarTodas();
        assertEquals(1, list.size());
    }

    @Test
    void shouldStartProductionAndDeductMaterials() {
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        // 10 units * 1.5 of material = 15 total needed
        OrdemProducaoResponse response = service.iniciarProducao(mockOp.getId());
        assertNotNull(response);

        assertEquals(OrdemProducaoStatus.CORTE, mockOp.getStatus());
        assertNotNull(mockOp.getDataInicio());

        // Verify inventory service was called correctly for the explosion
        verify(estoqueMovimentacaoService, times(1)).registrarMovimentacao(
                any(UUID.class),
                eq(TipoMovimentacao.SAIDA),
                eq(new BigDecimal("15.0")),
                eq("OP: OP-001")
        );
    }
    
    @Test
    void shouldThrowExceptionWhenStartingStartedOp() {
        mockOp.setStatus(OrdemProducaoStatus.CORTE);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        
        assertThrows(IllegalStateException.class, () -> service.iniciarProducao(mockOp.getId()));
    }

    @Test
    void shouldGerarPacotes() {
        // Setup
        mockOp.setStatus(OrdemProducaoStatus.CORTE);
        
        com.erp.catalog.domain.ProdutoSku sku = new com.erp.catalog.domain.ProdutoSku();
        sku.setId(UUID.randomUUID());
        
        com.erp.production.domain.OrdemProducaoItem item = new com.erp.production.domain.OrdemProducaoItem();
        item.setQuantidade(30);
        item.setProdutoSku(sku);
        mockOp.addItem(item);

        com.erp.production.domain.FichaTecnicaOperacao operacao = new com.erp.production.domain.FichaTecnicaOperacao();
        operacao.setOrdemExecucao(1);
        operacao.setNome("Costura");
        operacao.setTempoCalculadoCentesimal(new BigDecimal("2.0"));
        mockFicha.getOperacoes().add(operacao);

        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(pacoteRepository.findByOrdemProducaoId(mockOp.getId())).thenReturn(new ArrayList<>());
        when(pacoteRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        
        service.gerarPacotes(mockOp.getId(), 20);

        // 30 items with size 20 = 2 packages
        verify(pacoteRepository, times(2)).save(any(com.erp.production.domain.Pacote.class));
        verify(cupomRepository, times(2)).save(any(com.erp.production.domain.Cupom.class));
    }
}
