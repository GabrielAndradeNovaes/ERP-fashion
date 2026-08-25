package com.erp.core.service.impl;

import com.erp.core.domain.Fornecedor;
import com.erp.core.dto.FornecedorRequest;
import com.erp.core.dto.FornecedorResponse;
import com.erp.core.repository.FornecedorRepository;
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
public class FornecedorServiceImplTest {

    @Mock
    private FornecedorRepository repository;

    @InjectMocks
    private FornecedorServiceImpl service;

    private Fornecedor mockFornecedor;
    private FornecedorRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockFornecedor = new Fornecedor();
        mockFornecedor.setId(UUID.randomUUID());
        mockFornecedor.setNome("Fornecedor Teste");
        mockFornecedor.setDocumento("98765432100");
        mockFornecedor.setEmail("fornecedor@teste.com");
        mockFornecedor.setTelefone("11888888888");

        mockRequest = new FornecedorRequest(
                "Fornecedor Teste",
                "98765432100",
                "fornecedor@teste.com",
                "11888888888",
                null,
                null
        );
    }

    @Test
    void shouldCreateSuccessfully() {
        when(repository.save(any(Fornecedor.class))).thenReturn(mockFornecedor);

        FornecedorResponse response = service.create(mockRequest);

        assertNotNull(response);
        assertEquals("Fornecedor Teste", response.nome());
        verify(repository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(mockFornecedor));

        List<FornecedorResponse> responses = service.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetById() {
        UUID id = mockFornecedor.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockFornecedor));

        FornecedorResponse response = service.getById(id);

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
        UUID id = mockFornecedor.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockFornecedor));
        when(repository.save(any(Fornecedor.class))).thenReturn(mockFornecedor);

        FornecedorResponse response = service.update(id, mockRequest);

        assertNotNull(response);
        assertEquals("Fornecedor Teste", response.nome());
        verify(repository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    void shouldDeleteSuccessfully() {
        UUID id = mockFornecedor.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.delete(id));
        verify(repository, times(1)).deleteById(id);
    }
}
