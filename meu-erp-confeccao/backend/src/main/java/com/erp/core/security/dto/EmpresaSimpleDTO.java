package com.erp.core.security.dto;

import java.util.UUID;

public class EmpresaSimpleDTO {
    private UUID id;
    private String nomeFantasia;

    public EmpresaSimpleDTO() {}

    public EmpresaSimpleDTO(UUID id, String nomeFantasia) {
        this.id = id;
        this.nomeFantasia = nomeFantasia;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
}
