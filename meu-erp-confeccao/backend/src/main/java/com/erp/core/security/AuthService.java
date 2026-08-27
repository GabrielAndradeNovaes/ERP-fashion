package com.erp.core.security;

import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final DataSource dataSource;

    public AuthService(AuthenticationManager authenticationManager, 
                       UserDetailsServiceImpl userDetailsService, 
                       JwtService jwtService,
                       DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.dataSource = dataSource;
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(request.getEmail());
        
        java.util.List<String> empresas = getEmpresas(userDetails.getUsuario().getId().toString(), userDetails.getTenantId());
        userDetails.setEmpresas(empresas);

        String jwtToken = jwtService.generateToken(userDetails);
        
        String tenantStatus = getTenantStatus(userDetails.getTenantId());

        String filialId = userDetails.getUsuario().getFilialPrincipalId() != null ? userDetails.getUsuario().getFilialPrincipalId().toString() : null;
        java.util.List<String> permissoes = userDetails.getUsuario().getPermissoes() != null 
            ? new java.util.ArrayList<>(userDetails.getUsuario().getPermissoes()) 
            : new java.util.ArrayList<>();

        return new AuthResponse(
                jwtToken,
                userDetails.getUsuario().getNome(),
                userDetails.getUsuario().getEmail(),
                userDetails.getUsuario().getRole(),
                userDetails.getTenantId(),
                tenantStatus,
                empresas,
                filialId,
                permissoes
        );
    }

    private String getTenantStatus(String schemaName) {
        String sql = "SELECT status FROM master.clientes_tenant WHERE schema_name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, schemaName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar status do tenant: " + e.getMessage());
        }
        return "ATIVO"; // fallback
    }

    private java.util.List<String> getEmpresas(String usuarioId, String schemaName) {
        java.util.List<String> empresas = new java.util.ArrayList<>();
        String sql = "SELECT empresa_id FROM " + schemaName + ".usuario_empresas WHERE usuario_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(usuarioId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empresas.add(rs.getString("empresa_id"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar empresas do usuario: " + e.getMessage());
        }
        return empresas;
    }
}
