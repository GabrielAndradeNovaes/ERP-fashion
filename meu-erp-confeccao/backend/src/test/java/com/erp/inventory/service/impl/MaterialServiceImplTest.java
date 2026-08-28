package com.erp.inventory.service.impl;

import com.erp.inventory.domain.Material;
import com.erp.inventory.dto.MaterialRequest;
import com.erp.inventory.dto.MaterialResponse;
import com.erp.inventory.repository.MaterialRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceImplTest {

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private MaterialServiceImpl materialService;

    private Material materialMock;
    private MaterialRequest requestMock;

    @BeforeEach
    void setUp() {
        materialMock = new Material();
        materialMock.setId(UUID.randomUUID());
        materialMock.setCodigo("MAT-001");
        materialMock.setNome("Tecido Algodão");
        materialMock.setUnidadeMedida("METRO");
        materialMock.setCustoUnitario(new BigDecimal("15.50"));
        materialMock.setQuantidadeAtual(BigDecimal.ZERO);

        requestMock = new MaterialRequest(
                "MAT-001",
                "Tecido Algodão",
                "Tecido 100% algodão branco",
                "METRO",
                new BigDecimal("15.50"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void shouldCreateMaterialSuccessfully() {
        when(materialRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(materialRepository.save(any(Material.class))).thenReturn(materialMock);

        MaterialResponse response = materialService.createMaterial(requestMock);

        assertNotNull(response);
        assertEquals("MAT-001", response.codigo());
        assertEquals("Tecido Algodão", response.nome());
        verify(materialRepository, times(1)).findByCodigo("MAT-001");
        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void shouldThrowExceptionWhenCreateMaterialWithExistingCodigo() {
        when(materialRepository.findByCodigo(anyString())).thenReturn(Optional.of(materialMock));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            materialService.createMaterial(requestMock);
        });

        assertTrue(exception.getMessage().contains("já existe"));
        verify(materialRepository, times(1)).findByCodigo("MAT-001");
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void shouldGetAllMateriais() {
        when(materialRepository.findAll()).thenReturn(List.of(materialMock));

        List<MaterialResponse> responses = materialService.getAllMateriais();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("MAT-001", responses.get(0).codigo());
        verify(materialRepository, times(1)).findAll();
    }

    @Test
    void shouldGetMaterialById() {
        UUID id = materialMock.getId();
        when(materialRepository.findById(id)).thenReturn(Optional.of(materialMock));

        MaterialResponse response = materialService.getMaterialById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        verify(materialRepository, times(1)).findById(id);
    }
}
