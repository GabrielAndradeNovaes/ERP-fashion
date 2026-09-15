package com.erp.core.tenant;

import com.erp.core.security.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "acessos_logs")
public class AcessoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_acesso", nullable = false)
    private LocalDateTime dataAcesso;

    @Column(length = 50)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        if (this.dataAcesso == null) {
            this.dataAcesso = LocalDateTime.now();
        }
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getDataAcesso() { return dataAcesso; }
    public void setDataAcesso(LocalDateTime dataAcesso) { this.dataAcesso = dataAcesso; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
