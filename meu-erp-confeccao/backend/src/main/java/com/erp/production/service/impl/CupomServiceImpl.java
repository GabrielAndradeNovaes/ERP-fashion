package com.erp.production.service.impl;

import com.erp.production.domain.Cupom;
import com.erp.production.dto.CupomResponse;
import com.erp.production.repository.CupomRepository;
import com.erp.production.service.CupomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CupomServiceImpl implements CupomService {

    private final CupomRepository cupomRepository;

    public CupomServiceImpl(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CupomResponse> listarPorOrdemProducao(UUID ordemProducaoId) {
        return cupomRepository.findByOrdemProducaoId(ordemProducaoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CupomResponse mapToResponse(Cupom cupom) {
        return new CupomResponse(
                cupom.getId(),
                cupom.getPacote().getOrdemProducao().getNumero(),
                cupom.getPacote().getSequencial(),
                cupom.getOperacao().getNome(),
                cupom.getCodigoBarras(),
                cupom.getTempoTotalCentesimal(),
                cupom.getPacote().getQuantidadePecas(),
                cupom.getStatus()
        );
    }
}
