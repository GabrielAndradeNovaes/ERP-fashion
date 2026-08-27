package com.erp.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
    private java.util.List<String> empresas;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEmpresas(java.util.List<String> empresas) {
        this.empresas = empresas;
    }

    public java.util.List<String> getEmpresas() {
        return empresas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTenantId() {
        return usuario.getTenantId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole()));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getAtivo();
    }
}
