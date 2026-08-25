package com.erp.core.domain;

import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "clientes")
@SQLDelete(sql = "UPDATE clientes SET deleted = true WHERE id=?")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;



    @Column(nullable = false)
    private String nome;

    @Column
    private String documento;

    @Column
    private String email;

    @Column
    private String telefone;

    @Column(nullable = false)
    private boolean deleted = false;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
