package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.domain.TipoTrajeto;
import com.erp.production.domain.DificuldadeTecido;
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
import com.erp.production.service.MotorCalculoSamService;
import com.erp.production.dto.CalculoSamInput;
import com.erp.production.dto.CalculoSamOutput;

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
    @Mock
    private MotorCalculoSamService motorCalculoSamService;

    @InjectMocks
    private FichaTecnicaServiceImpl service;

    private FichaTecnica mockFicha;
    private ProdutoBase mockProduto;
    private Material mockMaterial;
    private FichaTecnicaMaterial mockFichaMaterial;
    private FichaTecnicaOperacao mockOperacao;

    @BeforeEach
    void setUp() {
        mockProduto = new ProdutoBase();
        mockProduto.setId(UUID.randomUUID());
        mockProduto.setNome("Camiseta");
        mockProduto.setPrecoCusto(BigDecimal.ZERO);

        mockFicha = new FichaTecnica();
        mockFicha.setId(UUID.randomUUID());
        mockFicha.setProdutoBase(mockProduto);
        mockFicha.setVersao("v1");
        mockFicha.setMateriais(new ArrayList<>());
        mockFicha.setOperacoes(new ArrayList<>());

        mockMaterial = new Material();
        mockMaterial.setId(UUID.randomUUID());
        mockMaterial.setNome("Tecido Algodão");
        mockMaterial.setCustoUnitario(new BigDecimal("10.0"));

        mockFichaMaterial = new FichaTecnicaMaterial();
        mockFichaMaterial.setId(UUID.randomUUID());
        mockFichaMaterial.setMaterial(mockMaterial);
        mockFichaMaterial.setQuantidade(new BigDecimal("2.0"));
        
        mockOperacao = new FichaTecnicaOperacao();
        mockOperacao.setId(UUID.randomUUID());
        mockOperacao.setFichaTecnica(mockFicha);
        mockOperacao.setNome("Costura Reta");
    }

    @Test
    void shouldCreateFichaTecnica() {
        FichaTecnicaRequest request = new FichaTecnicaRequest(
                mockProduto.getId(), "v1", "obs", List.of(
                        new FichaTecnicaMaterialRequest(mockMaterial.getId(), new BigDecimal("2.0"))
                )
        );

        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));
        when(fichaTecnicaRepository.save(any(FichaTecnica.class))).thenAnswer(i -> {
            FichaTecnica ft = i.getArgument(0);
            ft.setId(UUID.randomUUID());
            return ft;
        });

        FichaTecnicaResponse response = service.createFichaTecnica(request);

        assertNotNull(response);
        assertEquals(1, response.materiais().size());
        verify(produtoBaseRepository, times(1)).save(mockProduto);
        assertEquals(new BigDecimal("20.00"), mockProduto.getPrecoCusto()); // 2.0 * 10.0
    }

    @Test
    void shouldThrowExceptionWhenCreateFichaTecnicaProdutoNotFound() {
        FichaTecnicaRequest request = new FichaTecnicaRequest(UUID.randomUUID(), "v1", null, null);
        when(produtoBaseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.createFichaTecnica(request));
    }
    
    @Test
    void shouldAddMaterial() {
        mockFicha.addMaterial(mockFichaMaterial);
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));
        when(fichaTecnicaRepository.save(any())).thenReturn(mockFicha);

        FichaTecnicaMaterialRequest request = new FichaTecnicaMaterialRequest(mockMaterial.getId(), new BigDecimal("3.0"));
        
        service.addMaterial(mockFicha.getId(), request);
        verify(produtoBaseRepository, times(1)).save(mockProduto);
    }
    
    @Test
    void shouldRemoveMaterial() {
        mockFicha.addMaterial(mockFichaMaterial);
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaRepository.save(any())).thenReturn(mockFicha);

        service.removeMaterial(mockFicha.getId(), mockFichaMaterial.getId());
        
        assertTrue(mockFicha.getMateriais().isEmpty());
        verify(produtoBaseRepository, times(1)).save(mockProduto);
    }
    
    @Test
    void shouldAddOperacao() {
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaRepository.save(any())).thenReturn(mockFicha);
        
        CalculoSamOutput mockOutput = new CalculoSamOutput();
        mockOutput.setSamMinutos(new BigDecimal("0.204125"));
        mockOutput.setParadasUtilizadas(2);
        when(motorCalculoSamService.calcularOperacao(any())).thenReturn(mockOutput);

        FichaTecnicaOperacaoRequest req = new FichaTecnicaOperacaoRequest(
                "Costura", "Reta", 1, 2, 0, 4000, new BigDecimal("4"), new BigDecimal("50"), TipoTrajeto.RETA, DificuldadeTecido.NORMAL
        );
        
        FichaTecnicaResponse res = service.addOperacao(mockFicha.getId(), req);
        
        assertEquals(1, mockFicha.getOperacoes().size());
        assertEquals(new BigDecimal("0.204125"), mockFicha.getOperacoes().get(0).getSamMinutos());
    }

    @Test
    void shouldRemoveOperacao() {
        mockFicha.addOperacao(mockOperacao);
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaOperacaoRepository.findById(mockOperacao.getId())).thenReturn(Optional.of(mockOperacao));
        when(fichaTecnicaRepository.save(any())).thenReturn(mockFicha);

        service.removeOperacao(mockFicha.getId(), mockOperacao.getId());
        
        assertTrue(mockFicha.getOperacoes().isEmpty());
    }

    @Test
    void shouldUpdateOperacao() {
        mockFicha.addOperacao(mockOperacao);
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaOperacaoRepository.findById(mockOperacao.getId())).thenReturn(Optional.of(mockOperacao));
        
        CalculoSamOutput mockOutput = new CalculoSamOutput();
        mockOutput.setSamMinutos(new BigDecimal("0.250000"));
        mockOutput.setParadasUtilizadas(3);
        when(motorCalculoSamService.calcularOperacao(any())).thenReturn(mockOutput);
        
        when(fichaTecnicaRepository.save(any())).thenReturn(mockFicha);

        FichaTecnicaOperacaoRequest req = new FichaTecnicaOperacaoRequest(
                "Costura Alterada", "MaquinaNova", 2, 2, 0, 3000, new BigDecimal("4"), new BigDecimal("60"), TipoTrajeto.CURVA, DificuldadeTecido.MALHA
        );

        FichaTecnicaResponse res = service.updateOperacao(mockFicha.getId(), mockOperacao.getId(), req);

        assertEquals("Costura Alterada", mockOperacao.getNome());
        assertEquals("MaquinaNova", mockOperacao.getMaquina());
        assertEquals(new BigDecimal("0.250000"), mockOperacao.getSamMinutos());
    }

    @Test
    void shouldThrowWhenUpdateOperacaoNotBelongsToFicha() {
        FichaTecnica outraFicha = new FichaTecnica();
        outraFicha.setId(UUID.randomUUID());
        mockOperacao.setFichaTecnica(outraFicha);

        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(fichaTecnicaOperacaoRepository.findById(mockOperacao.getId())).thenReturn(Optional.of(mockOperacao));

        FichaTecnicaOperacaoRequest req = new FichaTecnicaOperacaoRequest(
                "Costura", "Reta", 1, 2, 0, 4000, new BigDecimal("4"), new BigDecimal("50"), TipoTrajeto.RETA, DificuldadeTecido.NORMAL
        );

        assertThrows(IllegalArgumentException.class, () -> service.updateOperacao(mockFicha.getId(), mockOperacao.getId(), req));
    }

    @Test
    void shouldThrowWhenRemoveMaterialNotFound() {
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));

        assertThrows(IllegalArgumentException.class, () -> service.removeMaterial(mockFicha.getId(), UUID.randomUUID()));
    }
}
