package com.erp.finance.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import com.erp.finance.domain.TituloPagar;
import com.erp.finance.repository.TituloPagarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FinanceiroService {

    private final TituloPagarRepository tituloPagarRepository;

    public FinanceiroService(TituloPagarRepository tituloPagarRepository) {
        this.tituloPagarRepository = tituloPagarRepository;
    }

    @Transactional
    public TituloPagar criarTituloPagamentoFuncionario(Empresa empresa, Funcionario funcionario, BigDecimal valor, String descricao) {
        TituloPagar titulo = new TituloPagar();
        titulo.setEmpresa(empresa);
        titulo.setFuncionario(funcionario);
        titulo.setValor(valor);
        titulo.setDescricao(descricao);
        titulo.setDataVencimento(LocalDate.now()); // Vencimento imediato
        titulo.setStatus(TituloPagar.Status.PENDENTE);
        
        return tituloPagarRepository.save(titulo);
    }

    public List<TituloPagar> listarTitulos() {
        return tituloPagarRepository.findAll();
    }

    @Transactional
    public void baixarTitulo(UUID id) {
        TituloPagar titulo = tituloPagarRepository.findById(id).orElseThrow();
        titulo.setStatus(TituloPagar.Status.PAGO);
        titulo.setDataPagamento(LocalDate.now());
        tituloPagarRepository.save(titulo);
    }
}
