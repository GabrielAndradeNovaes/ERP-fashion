package com.erp.catalog.controller;

import com.erp.catalog.domain.Tamanho;
import com.erp.catalog.repository.TamanhoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TamanhoControllerTest {

    @Mock
    private TamanhoRepository tamanhoRepository;

    @InjectMocks
    private TamanhoController tamanhoController;

    private Tamanho tamanhoMock;

    @BeforeEach
    void setUp() {
        tamanhoMock = new Tamanho();
        tamanhoMock.setId(UUID.randomUUID());
        tamanhoMock.setSigla("M");
        tamanhoMock.setNome("Médio");
    }

    @Test
    void shouldCreateTamanho() {
        when(tamanhoRepository.save(any(Tamanho.class))).thenReturn(tamanhoMock);

        ResponseEntity<Tamanho> response = tamanhoController.create(tamanhoMock);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("M", response.getBody().getSigla());
    }

    @Test
    void shouldListTamanhos() {
        when(tamanhoRepository.findAll()).thenReturn(List.of(tamanhoMock));

        ResponseEntity<List<Tamanho>> response = tamanhoController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldUpdateTamanhoSuccessfully() {
        UUID id = tamanhoMock.getId();
        when(tamanhoRepository.findById(id)).thenReturn(Optional.of(tamanhoMock));
        when(tamanhoRepository.save(any(Tamanho.class))).thenReturn(tamanhoMock);

        Tamanho updateData = new Tamanho();
        updateData.setSigla("G");
        updateData.setNome("Grande");

        ResponseEntity<Tamanho> response = tamanhoController.update(id, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("G", response.getBody().getSigla());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTamanho() {
        UUID id = UUID.randomUUID();
        when(tamanhoRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Tamanho> response = tamanhoController.update(id, new Tamanho());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldDeleteTamanhoSuccessfully() {
        UUID id = tamanhoMock.getId();
        when(tamanhoRepository.existsById(id)).thenReturn(true);
        doNothing().when(tamanhoRepository).deleteById(id);

        ResponseEntity<Void> response = tamanhoController.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tamanhoRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTamanho() {
        UUID id = UUID.randomUUID();
        when(tamanhoRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Void> response = tamanhoController.delete(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tamanhoRepository, never()).deleteById(any());
    }
}
