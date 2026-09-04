package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.catalog.repository.ProdutoSkuRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.OrdemProducaoItem;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.domain.Pacote;
import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoItemRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.repository.CupomRepository;
import com.erp.production.repository.OrdemProducaoRepository;
import com.erp.production.repository.PacoteRepository;
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
    private EstoqueMovimentacaoService estoqueMovimentacaoService;
    @Mock
    private ProdutoSkuRepository produtoSkuRepository;
    @Mock
    private PacoteRepository pacoteRepository;
    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private OrdemProducaoServiceImpl service;

    private OrdemProducao mockOp;
    private ProdutoBase mockProduto;
    private ProdutoSku mockSku;
    private FichaTecnica mockFicha;
    private OrdemProducaoRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockProduto = new ProdutoBase();
        mockProduto.setId(UUID.randomUUID());
        mockProduto.setNome("Camiseta");

        mockSku = new ProdutoSku();
        mockSku.setId(UUID.randomUUID());
        mockSku.setCor("Preto");
        mockSku.setTamanho("M");
        mockSku.setProdutoBase(mockProduto);
        mockSku.setQuantidadeAtual(10);
        
        List<ProdutoSku> skus = new ArrayList<>();
        skus.add(mockSku);
        mockProduto.setSkus(skus);

        mockFicha = new FichaTecnica();
        mockFicha.setId(UUID.randomUUID());
        mockFicha.setVersao("v1");
        mockFicha.setMateriais(new ArrayList<>());
        mockFicha.setOperacoes(new ArrayList<>());
        
        mockProduto.setFichaTecnica(mockFicha);

        Material mat = new Material();
        mat.setId(UUID.randomUUID());
        
        FichaTecnicaMaterial ftm = new FichaTecnicaMaterial();
        ftm.setMaterial(mat);
        ftm.setQuantidade(new BigDecimal("1.5"));
        mockFicha.addMaterial(ftm);

        FichaTecnicaOperacao opc = new FichaTecnicaOperacao();
        opc.setId(UUID.randomUUID());
        opc.setTempoCalculadoCentesimal(new BigDecimal("10.0"));
        mockFicha.addOperacao(opc);

        mockOp = new OrdemProducao();
        mockOp.setId(UUID.randomUUID());
        mockOp.setNumero("OP-001");
        mockOp.setProdutoBase(mockProduto);
        mockOp.setFichaTecnica(mockFicha);
        mockOp.setQuantidade(10);
        mockOp.setStatus(OrdemProducaoStatus.PENDENTE);
        
        OrdemProducaoItem mockItem = new OrdemProducaoItem();
        mockItem.setProdutoSku(mockSku);
        mockItem.setQuantidade(10);
        mockOp.addItem(mockItem);

        mockRequest = new OrdemProducaoRequest("OP-001", mockProduto.getId(), 10, List.of(
            new OrdemProducaoItemRequest(mockSku.getId(), 10)
        ));
    }

    @Test
    void shouldCriarOrdemProducao() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(false);
        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        when(produtoSkuRepository.findById(mockSku.getId())).thenReturn(Optional.of(mockSku));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        OrdemProducaoResponse response = service.criarOrdemProducao(mockRequest);

        assertNotNull(response);
        assertEquals("OP-001", response.numero());
        verify(ordemProducaoRepository, times(1)).save(any(OrdemProducao.class));
    }

    @Test
    void shouldThrowWhenCriarOrdemProducaoExists() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.criarOrdemProducao(mockRequest));
    }
    
    @Test
    void shouldThrowWhenCriarOrdemProducaoProdutoSemFicha() {
        mockProduto.setFichaTecnica(null);
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(false);
        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        
        assertThrows(IllegalArgumentException.class, () -> service.criarOrdemProducao(mockRequest));
    }

    @Test
    void shouldIniciarProducao() {
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        OrdemProducaoResponse response = service.iniciarProducao(mockOp.getId());

        assertNotNull(response);
        verify(estoqueMovimentacaoService, times(1)).registrarMovimentacao(
                any(), eq(TipoMovimentacao.SAIDA), any(), anyString()
        );
        assertEquals(OrdemProducaoStatus.EM_ANDAMENTO, mockOp.getStatus());
    }

    @Test
    void shouldThrowWhenIniciarProducaoNotPendente() {
        mockOp.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));

        assertThrows(IllegalStateException.class, () -> service.iniciarProducao(mockOp.getId()));
    }

    @Test
    void shouldListarTodas() {
        when(ordemProducaoRepository.findAll()).thenReturn(List.of(mockOp));
        List<OrdemProducaoResponse> list = service.listarTodas();
        assertEquals(1, list.size());
    }

    @Test
    void shouldAtualizarStatusParaConcluida() {
        mockOp.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        service.atualizarStatus(mockOp.getId(), OrdemProducaoStatus.CONCLUIDA);

        assertEquals(OrdemProducaoStatus.CONCLUIDA, mockOp.getStatus());
        verify(produtoSkuRepository, times(1)).save(mockSku);
        assertEquals(20, mockSku.getQuantidadeAtual()); // 10 original + 10 da OP
    }
    
    @Test
    void shouldEstornarOrdemProducao() {
        mockOp.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        service.estornarOrdemProducao(mockOp.getId());

        assertEquals(OrdemProducaoStatus.PENDENTE, mockOp.getStatus());
        verify(estoqueMovimentacaoService, times(1)).registrarMovimentacao(
                any(), eq(TipoMovimentacao.ENTRADA), any(), anyString()
        );
    }
    
    @Test
    void shouldEstornarOrdemProducaoConcluida() {
        mockOp.setStatus(OrdemProducaoStatus.CONCLUIDA);
        mockSku.setQuantidadeAtual(20);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        service.estornarOrdemProducao(mockOp.getId());

        assertEquals(OrdemProducaoStatus.PENDENTE, mockOp.getStatus());
        verify(produtoSkuRepository, times(1)).save(mockSku);
        assertEquals(10, mockSku.getQuantidadeAtual()); // 20 - 10 da OP
    }

    @Test
    void shouldGerarPacotes() {
        mockOp.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(pacoteRepository.findByOrdemProducaoId(mockOp.getId())).thenReturn(new ArrayList<>());
        
        Pacote mockPacote = new Pacote();
        mockPacote.setSequencial(1);
        when(pacoteRepository.save(any())).thenReturn(mockPacote);

        service.gerarPacotes(mockOp.getId(), 5);

        verify(pacoteRepository, times(2)).save(any(Pacote.class)); // 10 itens / 5 por pacote = 2 pacotes
        verify(cupomRepository, times(2)).save(any()); // 1 operacao * 2 pacotes = 2 cupons
    }
}
