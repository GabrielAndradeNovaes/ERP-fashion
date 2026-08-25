package com.erp.core.service.impl;

import com.erp.core.domain.UnidadeMedida;
import com.erp.core.dto.UnidadeMedidaRequest;
import com.erp.core.dto.UnidadeMedidaResponse;
import com.erp.core.repository.UnidadeMedidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnidadeMedidaServiceImplTest {

    @Mock
    private UnidadeMedidaRepository repository;

    @InjectMocks
    private UnidadeMedidaServiceImpl service;

    private UnidadeMedida mockUnidadeMedida;
    private UnidadeMedidaRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUnidadeMedida = new UnidadeMedida();
        mockUnidadeMedida.setId(UUID.randomUUID());
        mockUnidadeMedida.setNome("Metro");
        mockUnidadeMedida.setSigla("m");

        mockRequest = new UnidadeMedidaRequest(
                "Metro",
                null,
                null,
                null,
                null,
                "m"
        );
    }

    @Test
    void shouldCreateSuccessfully() {
        when(repository.save(any(UnidadeMedida.class))).thenReturn(mockUnidadeMedida);

        UnidadeMedidaResponse response = service.create(mockRequest);

        assertNotNull(response);
        assertEquals("Metro", response.nome());
        assertEquals("m", response.sigla());
        verify(repository, times(1)).save(any(UnidadeMedida.class));
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(mockUnidadeMedida));

        List<UnidadeMedidaResponse> responses = service.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetById() {
        UUID id = mockUnidadeMedida.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockUnidadeMedida));

        UnidadeMedidaResponse response = service.getById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenGetByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getById(id));
    }

    @Test
    void shouldUpdateSuccessfully() {
        UUID id = mockUnidadeMedida.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockUnidadeMedida));
        when(repository.save(any(UnidadeMedida.class))).thenReturn(mockUnidadeMedida);

        UnidadeMedidaResponse response = service.update(id, mockRequest);

        assertNotNull(response);
        assertEquals("Metro", response.nome());
        verify(repository, times(1)).save(any(UnidadeMedida.class));
    }

    @Test
    void shouldDeleteSuccessfully() {
        UUID id = mockUnidadeMedida.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.delete(id));
        verify(repository, times(1)).deleteById(id);
    }
}
