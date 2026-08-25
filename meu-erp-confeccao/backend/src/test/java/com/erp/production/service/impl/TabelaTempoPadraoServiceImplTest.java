package com.erp.production.service.impl;

import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.domain.GrauDificuldade;
import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.dto.TabelaTempoPadraoRequest;
import com.erp.production.dto.TabelaTempoPadraoResponse;
import com.erp.production.repository.TabelaTempoPadraoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TabelaTempoPadraoServiceImplTest {

    @Mock
    private TabelaTempoPadraoRepository repository;

    @InjectMocks
    private TabelaTempoPadraoServiceImpl service;

    private TabelaTempoPadrao mockTabela;
    private TabelaTempoPadraoRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockTabela = new TabelaTempoPadrao();
        mockTabela.setId(UUID.randomUUID());
        mockTabela.setIndice(1);
        mockTabela.setGrauDificuldade(GrauDificuldade.FACIL);
        mockTabela.setFaixaComprimento(FaixaComprimentoCostura.DE_0_A_60);
        mockTabela.setTempoCentesimal(new BigDecimal("0.5"));

        mockRequest = new TabelaTempoPadraoRequest(
                1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60, new BigDecimal("0.5")
        );
    }

    @Test
    void shouldCreateSuccessfully() {
        when(repository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60))
                .thenReturn(Optional.empty());
        when(repository.save(any(TabelaTempoPadrao.class))).thenReturn(mockTabela);

        TabelaTempoPadraoResponse response = service.create(mockRequest);

        assertNotNull(response);
        verify(repository, times(1)).save(any(TabelaTempoPadrao.class));
    }

    @Test
    void shouldThrowExceptionWhenCreateDuplicated() {
        when(repository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60))
                .thenReturn(Optional.of(mockTabela));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.create(mockRequest));
        assertTrue(ex.getMessage().contains("cadastrado"));
    }

    @Test
    void shouldUpdateSuccessfully() {
        UUID id = mockTabela.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockTabela));
        when(repository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60))
                .thenReturn(Optional.of(mockTabela)); // same id is fine
        when(repository.save(any(TabelaTempoPadrao.class))).thenReturn(mockTabela);

        TabelaTempoPadraoResponse response = service.update(id, mockRequest);

        assertNotNull(response);
        verify(repository, times(1)).save(any(TabelaTempoPadrao.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateDuplicatedAnotherId() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new TabelaTempoPadrao()));
        when(repository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(1, GrauDificuldade.FACIL, FaixaComprimentoCostura.DE_0_A_60))
                .thenReturn(Optional.of(mockTabela)); // different id found in uniqueness check

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.update(id, mockRequest));
        assertTrue(ex.getMessage().contains("cadastrado"));
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(mockTabela));
        List<TabelaTempoPadraoResponse> list = service.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void shouldDelete() {
        doNothing().when(repository).deleteById(mockTabela.getId());
        assertDoesNotThrow(() -> service.delete(mockTabela.getId()));
        verify(repository, times(1)).deleteById(mockTabela.getId());
    }
}
