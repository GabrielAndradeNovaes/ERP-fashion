package com.erp.core.controller;

import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioControllerTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private FuncionarioController controller;

    @Test
    void testListarTodos() {
        List<Funcionario> list = Collections.singletonList(new Funcionario());
        when(repository.findAll()).thenReturn(list);
        ResponseEntity<List<Funcionario>> result = controller.listarTodos();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testCriar() {
        Funcionario f = new Funcionario();
        when(repository.save(f)).thenReturn(f);
        ResponseEntity<Funcionario> result = controller.criar(f);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(f, result.getBody());
    }

    @Test
    void testAtualizar_Found() {
        UUID id = UUID.randomUUID();
        Funcionario existing = new Funcionario();
        Funcionario updated = new Funcionario();
        updated.setNome("Nome");
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ResponseEntity<Funcionario> result = controller.atualizar(id, updated);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Nome", existing.getNome());
    }

    @Test
    void testAtualizar_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Funcionario> result = controller.atualizar(id, new Funcionario());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testDeletar_Found() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);
        ResponseEntity<Void> result = controller.deletar(id);
        verify(repository).deleteById(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testDeletar_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);
        ResponseEntity<Void> result = controller.deletar(id);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}
