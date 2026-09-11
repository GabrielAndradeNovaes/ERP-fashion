package com.erp.finance.repository;

import com.erp.finance.domain.TransacaoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface TransacaoPagamentoRepository extends JpaRepository<TransacaoPagamento, UUID> {
    Optional<TransacaoPagamento> findByGatewayTransacaoId(String gatewayTransacaoId);
}
