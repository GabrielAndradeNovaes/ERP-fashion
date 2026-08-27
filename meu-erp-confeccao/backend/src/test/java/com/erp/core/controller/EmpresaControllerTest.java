package com.erp.core.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.repository.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpresaControllerTest {

    @Mock
    private EmpresaRepository repository;

    @InjectMocks
    private EmpresaController controller;

    @Test
    void testListar() {
        List<Empresa> list = Collections.singletonList(new Empresa());
        when(repository.findAll()).thenReturn(list);
        List<Empresa> result = controller.listar();
        assertEquals(list, result);
    }

    @Test
    void testBuscar_Found() {
        UUID id = UUID.randomUUID();
        Empresa e = new Empresa();
        when(repository.findById(id)).thenReturn(Optional.of(e));
        ResponseEntity<Empresa> result = controller.buscar(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(e, result.getBody());
    }

    @Test
    void testBuscar_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Empresa> result = controller.buscar(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testCriar() {
        Empresa e = new Empresa();
        when(repository.save(e)).thenReturn(e);
        Empresa result = controller.criar(e);
        assertTrue(result.getAtivo());
        verify(repository).save(e);
    }

    @Test
    void testAtualizar_Found() {
        UUID id = UUID.randomUUID();
        Empresa existing = new Empresa();
        Empresa updated = new Empresa();
        updated.setNomeFantasia("Nome");
        
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        
        ResponseEntity<Empresa> result = controller.atualizar(id, updated);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Nome", existing.getNomeFantasia());
    }

    @Test
    void testAtualizar_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Empresa> result = controller.atualizar(id, new Empresa());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testDeletar_Found() {
        UUID id = UUID.randomUUID();
        Empresa existing = new Empresa();
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        ResponseEntity<Void> result = controller.deletar(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(repository).save(existing);
    }

    @Test
    void testDeletar_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Void> result = controller.deletar(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}
