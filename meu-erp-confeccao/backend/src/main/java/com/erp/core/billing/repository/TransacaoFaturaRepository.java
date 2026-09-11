package com.erp.core.billing.repository;

import com.erp.core.billing.domain.TransacaoFatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoFaturaRepository extends JpaRepository<TransacaoFatura, UUID> {
    TransacaoFatura findByGatewayTransacaoId(String gatewayTransacaoId);
}
