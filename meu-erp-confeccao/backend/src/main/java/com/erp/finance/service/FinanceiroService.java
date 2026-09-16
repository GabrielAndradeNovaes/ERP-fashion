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
    private final com.erp.finance.repository.TituloReceberRepository tituloReceberRepository;
    private final com.erp.core.repository.EmpresaRepository empresaRepository;
    private final com.erp.core.repository.FuncionarioRepository funcionarioRepository;
    private final com.erp.core.repository.ClienteRepository clienteRepository;

    public FinanceiroService(TituloPagarRepository tituloPagarRepository, 
                             com.erp.finance.repository.TituloReceberRepository tituloReceberRepository,
                             com.erp.core.repository.EmpresaRepository empresaRepository,
                             com.erp.core.repository.FuncionarioRepository funcionarioRepository,
                             com.erp.core.repository.ClienteRepository clienteRepository) {
        this.tituloPagarRepository = tituloPagarRepository;
        this.tituloReceberRepository = tituloReceberRepository;
        this.empresaRepository = empresaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
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

    public List<com.erp.finance.domain.TituloReceber> listarTitulosReceber() {
        return tituloReceberRepository.findAll();
    }

    @Transactional
    public void baixarTituloReceber(UUID id) {
        com.erp.finance.domain.TituloReceber titulo = tituloReceberRepository.findById(id).orElseThrow();
        titulo.setStatus(com.erp.finance.domain.TituloReceber.Status.RECEBIDO);
        titulo.setDataPagamento(LocalDate.now());
        tituloReceberRepository.save(titulo);
    }

    @Transactional
    public TituloPagar criarTituloPagarManual(com.erp.finance.dto.TituloRequest request) {
        java.util.List<UUID> empresas = com.erp.core.tenant.EmpresaContext.getEmpresas();
        if (empresas.isEmpty()) throw new IllegalStateException("Nenhuma empresa no contexto");
        
        Empresa empresa = empresaRepository.findById(empresas.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        TituloPagar titulo = new TituloPagar();
        titulo.setEmpresa(empresa);
        titulo.setDescricao(request.descricao());
        titulo.setValor(request.valor());
        titulo.setDataEmissao(request.dataEmissao() != null ? request.dataEmissao() : LocalDate.now());
        titulo.setDataVencimento(request.dataVencimento() != null ? request.dataVencimento() : LocalDate.now());
        titulo.setStatus(TituloPagar.Status.PENDENTE);
        
        if (request.funcionarioId() != null) {
            titulo.setFuncionario(funcionarioRepository.findById(request.funcionarioId()).orElse(null));
        }
        
        return tituloPagarRepository.save(titulo);
    }

    @Transactional
    public com.erp.finance.domain.TituloReceber criarTituloReceberManual(com.erp.finance.dto.TituloRequest request) {
        java.util.List<UUID> empresas = com.erp.core.tenant.EmpresaContext.getEmpresas();
        if (empresas.isEmpty()) throw new IllegalStateException("Nenhuma empresa no contexto");
        
        Empresa empresa = empresaRepository.findById(empresas.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        com.erp.finance.domain.TituloReceber titulo = new com.erp.finance.domain.TituloReceber();
        titulo.setEmpresa(empresa);
        titulo.setDescricao(request.descricao());
        titulo.setValor(request.valor());
        titulo.setDataEmissao(request.dataEmissao() != null ? request.dataEmissao() : LocalDate.now());
        titulo.setDataVencimento(request.dataVencimento() != null ? request.dataVencimento() : LocalDate.now());
        titulo.setStatus(com.erp.finance.domain.TituloReceber.Status.PENDENTE);
        
        if (request.clienteId() != null) {
            titulo.setCliente(clienteRepository.findById(request.clienteId()).orElse(null));
        }
        
        return tituloReceberRepository.save(titulo);
    }
}
