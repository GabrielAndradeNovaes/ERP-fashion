package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.domain.GrauDificuldade;
import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.dto.FichaTecnicaMaterialRequest;
import com.erp.production.dto.FichaTecnicaOperacaoRequest;
import com.erp.production.dto.FichaTecnicaRequest;
import com.erp.production.dto.FichaTecnicaResponse;
import com.erp.production.repository.FichaTecnicaOperacaoRepository;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.TabelaTempoPadraoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FichaTecnicaServiceImplTest {

    @Mock
    private FichaTecnicaRepository fichaTecnicaRepository;
    @Mock
    private ProdutoBaseRepository produtoBaseRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private TabelaTempoPadraoRepository tabelaTempoPadraoRepository;
    @Mock
    private FichaTecnicaOperacaoRepository fichaTecnicaOperacaoRepository;

    @InjectMocks
    private FichaTecnicaServiceImpl service;

    private ProdutoBase mockProduto;
    private Material mockMaterial;
    private FichaTecnica mockFicha;

    @BeforeEach
    void setUp() {
        mockProduto = new ProdutoBase();
        mockProduto.setId(UUID.randomUUID());
        mockProduto.setNome("Camiseta");
        mockProduto.setPrecoCusto(BigDecimal.ZERO);

        mockMaterial = new Material();
        mockMaterial.setId(UUID.randomUUID());
        mockMaterial.setNome("Tecido Algodão");
        mockMaterial.setCustoUnitario(new BigDecimal("10.50"));
        mockMaterial.setUnidadeMedida("m");

        mockFicha = new FichaTecnica();
        mockFicha.setId(UUID.randomUUID());
        mockFicha.setProdutoBase(mockProduto);
        mockFicha.setVersao("v1.0");
        mockFicha.setMateriais(new ArrayList<>());
        mockFicha.setOperacoes(new ArrayList<>());
    }

    @Test
    void shouldCreateFichaTecnicaSuccessfully() {
        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));
        
        when(fichaTecnicaRepository.save(any(FichaTecnica.class))).thenAnswer(i -> {
            FichaTecnica f = i.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        FichaTecnicaMaterialRequest matReq = new FichaTecnicaMaterialRequest(mockMaterial.getId(), new BigDecimal("2.0"));
        FichaTecnicaRequest request = new FichaTecnicaRequest(
                mockProduto.getId(),
                "v1.0",
                "Obs",
                List.of(matReq)
        );

        FichaTecnicaResponse response = service.createFichaTecnica(request);

        assertNotNull(response);
        assertEquals("v1.0", response.versao());
        assertEquals(0, new BigDecimal("21.00").compareTo(response.custoTotalMateriais())); // 2.0 * 10.50
        
        verify(fichaTecnicaRepository, times(1)).save(any(FichaTecnica.class));
        verify(produtoBaseRepository, times(1)).save(any(ProdutoBase.class)); // Update custo in produto
    }

    @Test
    void shouldThrowExceptionWhenProdutoNotFound() {
        UUID produtoId = UUID.randomUUID();
        when(produtoBaseRepository.findById(produtoId)).thenReturn(Optional.empty());

        FichaTecnicaRequest request = new FichaTecnicaRequest(produtoId, "v1.0", "Obs", null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createFichaTecnica(request));
        assertTrue(ex.getMessage().contains("ncontrado"));
    }

    @Test
    void shouldGetFichasPorProduto() {
        when(fichaTecnicaRepository.findAll()).thenReturn(List.of(mockFicha));

        List<FichaTecnicaResponse> responses = service.getFichasPorProduto(mockProduto.getId());

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void shouldGetFichaTecnicaById() {
        UUID id = mockFicha.getId();
        when(fichaTecnicaRepository.findById(id)).thenReturn(Optional.of(mockFicha));

        FichaTecnicaResponse response = service.getFichaTecnicaById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
    }

    @Test
    void shouldAddOperacaoSuccessfully() {
        UUID fichaId = mockFicha.getId();
        when(fichaTecnicaRepository.findById(fichaId)).thenReturn(Optional.of(mockFicha));
        
        TabelaTempoPadrao tp = new TabelaTempoPadrao();
        tp.setTempoCentesimal(new BigDecimal("1.25"));
        when(tabelaTempoPadraoRepository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(2, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60))
                .thenReturn(Optional.of(tp));

        when(fichaTecnicaRepository.save(any(FichaTecnica.class))).thenReturn(mockFicha);

        FichaTecnicaOperacaoRequest req = new FichaTecnicaOperacaoRequest(
                "Costurar", "Reta", 1, 1, 1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60
        );

        FichaTecnicaResponse response = service.addOperacao(fichaId, req);

        assertNotNull(response);
        verify(fichaTecnicaRepository, times(1)).save(any(FichaTecnica.class));
    }

    @Test
    void shouldRemoveOperacaoSuccessfully() {
        UUID fichaId = mockFicha.getId();
        UUID operacaoId = UUID.randomUUID();
        
        FichaTecnicaOperacao op = new FichaTecnicaOperacao();
        op.setId(operacaoId);
        op.setFichaTecnica(mockFicha);
        mockFicha.addOperacao(op);

        when(fichaTecnicaRepository.findById(fichaId)).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaOperacaoRepository.findById(operacaoId)).thenReturn(Optional.of(op));
        when(fichaTecnicaRepository.save(any(FichaTecnica.class))).thenReturn(mockFicha);

        FichaTecnicaResponse response = service.removeOperacao(fichaId, operacaoId);

        assertNotNull(response);
        verify(fichaTecnicaRepository, times(1)).save(any(FichaTecnica.class));
    }
}
