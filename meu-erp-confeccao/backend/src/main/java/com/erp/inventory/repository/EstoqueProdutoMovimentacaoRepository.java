package com.erp.inventory.repository;

import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstoqueProdutoMovimentacaoRepository extends JpaRepository<EstoqueProdutoMovimentacao, UUID> {
    List<EstoqueProdutoMovimentacao> findBySkuIdOrderByDataMovimentacaoDesc(UUID skuId);
}
