package com.erp.core.service.impl;

import com.erp.core.domain.Cliente;
import com.erp.core.dto.ClienteRequest;
import com.erp.core.dto.ClienteResponse;
import com.erp.core.repository.ClienteRepository;
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
public class ClienteServiceImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente mockCliente;
    private ClienteRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockCliente = new Cliente();
        mockCliente.setId(UUID.randomUUID());
        mockCliente.setNome("Cliente Teste");
        mockCliente.setDocumento("12345678900");
        mockCliente.setEmail("cliente@teste.com");
        mockCliente.setTelefone("11999999999");

        mockRequest = new ClienteRequest(
                "Cliente Teste",
                "12345678900",
                "cliente@teste.com",
                "11999999999",
                null,
                null
        );
    }

    @Test
    void shouldCreateSuccessfully() {
        when(repository.save(any(Cliente.class))).thenReturn(mockCliente);

        ClienteResponse response = service.create(mockRequest);

        assertNotNull(response);
        assertEquals("Cliente Teste", response.nome());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void shouldGetAll() {
        when(repository.findAll()).thenReturn(List.of(mockCliente));

        List<ClienteResponse> responses = service.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetById() {
        UUID id = mockCliente.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockCliente));

        ClienteResponse response = service.getById(id);

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
        UUID id = mockCliente.getId();
        when(repository.findById(id)).thenReturn(Optional.of(mockCliente));
        when(repository.save(any(Cliente.class))).thenReturn(mockCliente);

        ClienteResponse response = service.update(id, mockRequest);

        assertNotNull(response);
        assertEquals("Cliente Teste", response.nome());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void shouldDeleteSuccessfully() {
        UUID id = mockCliente.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.delete(id));
        verify(repository, times(1)).deleteById(id);
    }
}
