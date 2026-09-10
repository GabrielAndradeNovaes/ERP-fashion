package com.erp.core.controller;

import com.erp.core.domain.Funcionario;
import com.erp.core.domain.FuncionarioJornada;
import com.erp.core.domain.GrupoFuncionario;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.core.repository.GrupoFuncionarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private GrupoFuncionarioRepository grupoFuncionarioRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private FuncionarioController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Funcionario funcionario;
    private GrupoFuncionario grupo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        grupo = new GrupoFuncionario();
        grupo.setId(UUID.randomUUID());
        grupo.setNome("Costureiras");

        funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());
        funcionario.setNome("Maria");
        funcionario.setMatricula("123");
        funcionario.setAtivo(true);
        funcionario.setGrupo(grupo);
        funcionario.setJornadas(new ArrayList<>());
    }

    @Test
    void shouldListarTodos() throws Exception {
        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));

        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Maria"));
    }

    @Test
    void shouldCriarComGrupoExistentePeloId() throws Exception {
        when(funcionarioRepository.save(any())).thenReturn(funcionario);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void shouldCriarComNovoGrupoPeloNome() throws Exception {
        GrupoFuncionario novoGrupo = new GrupoFuncionario();
        novoGrupo.setNome("Novo Grupo");
        funcionario.setGrupo(novoGrupo);
        
        // Ensure mock returns empty list for existing groups, triggering save
        when(grupoFuncionarioRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(new ArrayList<>());
        when(grupoFuncionarioRepository.save(any())).thenReturn(novoGrupo);
        when(funcionarioRepository.save(any())).thenReturn(funcionario);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isOk());
        
        verify(grupoFuncionarioRepository, times(1)).save(any());
    }
    
    @Test
    void shouldCriarSemGrupo() throws Exception {
        funcionario.setGrupo(null);
        when(funcionarioRepository.save(any())).thenReturn(funcionario);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAtualizarExistente() throws Exception {
        Funcionario atualizado = new Funcionario();
        atualizado.setNome("Maria Atualizada");
        
        FuncionarioJornada jornada = new FuncionarioJornada();
        jornada.setDiaSemana(1);
        atualizado.getJornadas().add(jornada);

        when(funcionarioRepository.findById(funcionario.getId())).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any())).thenReturn(funcionario);

        mockMvc.perform(put("/api/funcionarios/" + funcionario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAtualizarNaoExistente() throws Exception {
        when(funcionarioRepository.findById(any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/funcionarios/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCopiarJornada() throws Exception {
        FuncionarioJornada jornada = new FuncionarioJornada();
        jornada.setDiaSemana(1);
        jornada.setEntrada(LocalTime.of(8, 0));
        jornada.setSaida(LocalTime.of(17, 0));
        funcionario.getJornadas().add(jornada);

        Funcionario outroFuncionario = new Funcionario();
        outroFuncionario.setId(UUID.randomUUID());
        outroFuncionario.setGrupo(grupo);

        when(funcionarioRepository.findById(funcionario.getId())).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.findByGrupoId(grupo.getId())).thenReturn(List.of(funcionario, outroFuncionario));

        mockMvc.perform(post("/api/funcionarios/" + funcionario.getId() + "/copiar-jornada"))
                .andExpect(status().isOk());

        verify(funcionarioRepository, times(1)).saveAll(any());
        assertEquals(1, outroFuncionario.getJornadas().size()); // Recebeu a cópia
    }

    @Test
    void shouldReturnBadRequestWhenCopiarJornadaSemGrupo() throws Exception {
        funcionario.setGrupo(null);
        when(funcionarioRepository.findById(funcionario.getId())).thenReturn(Optional.of(funcionario));

        mockMvc.perform(post("/api/funcionarios/" + funcionario.getId() + "/copiar-jornada"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeletarExistente() throws Exception {
        when(funcionarioRepository.existsById(funcionario.getId())).thenReturn(true);

        mockMvc.perform(delete("/api/funcionarios/" + funcionario.getId()))
                .andExpect(status().isNoContent());

        verify(funcionarioRepository, times(1)).deleteById(funcionario.getId());
    }

    @Test
    void shouldReturnNotFoundWhenDeletarNaoExistente() throws Exception {
        when(funcionarioRepository.existsById(funcionario.getId())).thenReturn(false);

        mockMvc.perform(delete("/api/funcionarios/" + funcionario.getId()))
                .andExpect(status().isNotFound());
    }
}
