package com.erp.core.security;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios", schema = "master")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "filial_principal_id")
    private UUID filialPrincipalId;

    @Column(length = 20)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String cargo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 100)
    private String departamento;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_permissoes", 
        schema = "master", 
        joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "permissao")
    private Set<String> permissoes = new HashSet<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public UUID getFilialPrincipalId() { return filialPrincipalId; }
    public void setFilialPrincipalId(UUID filialPrincipalId) { this.filialPrincipalId = filialPrincipalId; }

    public Set<String> getPermissoes() { return permissoes; }
    public void setPermissoes(Set<String> permissoes) { this.permissoes = permissoes; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
