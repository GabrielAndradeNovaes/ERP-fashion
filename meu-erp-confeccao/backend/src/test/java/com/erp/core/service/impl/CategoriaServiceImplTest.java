package com.erp.core.service.impl;

import com.erp.core.domain.Categoria;
import com.erp.core.dto.CategoriaRequest;
import com.erp.core.dto.CategoriaResponse;
import com.erp.core.repository.CategoriaRepository;
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
public class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceImpl service;

    private Categoria mockCategoria;
    private CategoriaRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockCategoria = new Categoria();
        mockCategoria.setId(UUID.randomUUID());
        mockCategoria.setNome("Categoria Teste");
        mockCategoria.setTipo("TIPO_TESTE");

        mockRequest = new CategoriaRequest(
                "Categoria Teste",
                null,
                null,
                null,
                null,
                "TIPO_TESTE"
        );
    }

    @Test
    void shouldCreateSuccessfully() {
        when(repository.save(any(Categoria.class))).thenReturn(mockCategoria);

        CategoriaResponse response = service.create(mockRequest);

        assertNotNull(response);
        assertEquals("Categoria Teste", response.nome());
        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(mockCategoria));

        List<CategoriaResponse> responses = service.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetById() {
        UUID id = mockCategoria.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockCategoria));

        CategoriaResponse response = service.getById(id);

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
        UUID id = mockCategoria.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockCategoria));
        when(repository.save(any(Categoria.class))).thenReturn(mockCategoria);

        CategoriaResponse response = service.update(id, mockRequest);

        assertNotNull(response);
        assertEquals("Categoria Teste", response.nome());
        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void shouldDeleteSuccessfully() {
        UUID id = mockCategoria.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.delete(id));
        verify(repository, times(1)).deleteById(id);
    }
}
