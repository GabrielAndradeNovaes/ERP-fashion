package com.erp.catalog.controller;

import com.erp.catalog.domain.Cor;
import com.erp.catalog.repository.CorRepository;
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
public class CorControllerTest {

    @Mock
    private CorRepository corRepository;

    @InjectMocks
    private CorController corController;

    private Cor corMock;

    @BeforeEach
    void setUp() {
        corMock = new Cor();
        corMock.setId(UUID.randomUUID());
        corMock.setCodigoHex("#FFFFFF");
        corMock.setNome("Branco");
    }

    @Test
    void shouldCreateCor() {
        when(corRepository.save(any(Cor.class))).thenReturn(corMock);

        ResponseEntity<Cor> response = corController.create(corMock);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Branco", response.getBody().getNome());
    }

    @Test
    void shouldListCores() {
        when(corRepository.findAll()).thenReturn(List.of(corMock));

        ResponseEntity<List<Cor>> response = corController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldUpdateCorSuccessfully() {
        UUID id = corMock.getId();
        when(corRepository.findById(id)).thenReturn(Optional.of(corMock));
        when(corRepository.save(any(Cor.class))).thenReturn(corMock);

        Cor updateData = new Cor();
        updateData.setNome("Azul Marinho");

        ResponseEntity<Cor> response = corController.update(id, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Azul Marinho", response.getBody().getNome());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentCor() {
        UUID id = UUID.randomUUID();
        when(corRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Cor> response = corController.update(id, new Cor());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldDeleteCorSuccessfully() {
        UUID id = corMock.getId();
        when(corRepository.existsById(id)).thenReturn(true);
        doNothing().when(corRepository).deleteById(id);

        ResponseEntity<Void> response = corController.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(corRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCor() {
        UUID id = UUID.randomUUID();
        when(corRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Void> response = corController.delete(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(corRepository, never()).deleteById(any());
    }
}
