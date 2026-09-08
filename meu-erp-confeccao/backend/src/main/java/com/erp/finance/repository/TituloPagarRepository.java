package com.erp.finance.repository;

import com.erp.finance.domain.TituloPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TituloPagarRepository extends JpaRepository<TituloPagar, UUID> {
}
