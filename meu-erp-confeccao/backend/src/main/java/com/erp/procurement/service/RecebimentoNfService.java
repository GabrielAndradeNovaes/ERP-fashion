package com.erp.procurement.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.procurement.domain.*;
import com.erp.procurement.dto.RecebimentoNfResponse;
import com.erp.procurement.dto.RecebimentoNfItemResponse;
import com.erp.procurement.dto.xml.NfeProc;
import com.erp.procurement.repository.OrdemCompraRepository;
import com.erp.procurement.repository.RecebimentoNfRepository;
import com.erp.core.tenant.EmpresaContext;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecebimentoNfService {

    private final RecebimentoNfRepository recebimentoNfRepository;
    private final OrdemCompraRepository ordemCompraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EmpresaRepository empresaRepository;
    private final com.erp.inventory.repository.MaterialFornecedorRepository materialFornecedorRepository;
    private final com.erp.inventory.service.EstoqueMovimentacaoService estoqueMovimentacaoService;

    public RecebimentoNfService(RecebimentoNfRepository recebimentoNfRepository,
                                OrdemCompraRepository ordemCompraRepository,
                                FornecedorRepository fornecedorRepository,
                                EmpresaRepository empresaRepository,
                                com.erp.inventory.repository.MaterialFornecedorRepository materialFornecedorRepository,
                                com.erp.inventory.service.EstoqueMovimentacaoService estoqueMovimentacaoService) {
        this.recebimentoNfRepository = recebimentoNfRepository;
        this.ordemCompraRepository = ordemCompraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.empresaRepository = empresaRepository;
        this.materialFornecedorRepository = materialFornecedorRepository;
        this.estoqueMovimentacaoService = estoqueMovimentacaoService;
    }

    @Transactional(readOnly = true)
    public Page<RecebimentoNfResponse> listar(String chaveNfe, Pageable pageable) {
        Page<RecebimentoNf> recebimentos;
        if (chaveNfe != null && !chaveNfe.isEmpty()) {
            recebimentos = recebimentoNfRepository.findByChaveAcessoNfeContainingIgnoreCase(chaveNfe, pageable);
        } else {
            recebimentos = recebimentoNfRepository.findAll(pageable);
        }
        return recebimentos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public RecebimentoNfResponse buscarPorId(UUID id) {
        RecebimentoNf r = recebimentoNfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recebimento não encontrado"));
        return toResponse(r);
    }

    @Transactional
    public RecebimentoNfResponse processarXmlNfe(MultipartFile xmlFile, UUID ordemCompraId) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        NfeProc nfeProc = xmlMapper.readValue(xmlFile.getInputStream(), NfeProc.class);

        if (nfeProc.nfe == null || nfeProc.nfe.infNFe == null) {
            throw new RuntimeException("Formato de XML NFe inválido ou não suportado.");
        }

        NfeProc.InfNFe inf = nfeProc.nfe.infNFe;
        String chave = inf.id != null ? inf.id.replace("NFe", "") : "";
        String numNf = inf.ide != null ? inf.ide.nNF : "";
        String cnpjFornecedor = inf.emit != null ? inf.emit.cnpj : "";
        
        if (recebimentoNfRepository.findByChaveAcessoNfe(chave).isPresent()) {
            throw new RuntimeException("Nota Fiscal com chave " + chave + " já foi recebida.");
        }

        // Buscar a empresa do contexto
        List<UUID> empresas = EmpresaContext.getEmpresas();
        Empresa empresa;
        if (empresas != null && !empresas.isEmpty()) {
            empresa = empresaRepository.findById(empresas.get(0))
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada no banco"));
        } else {
            java.util.List<Empresa> todasEmpresas = empresaRepository.findAll();
            if (!todasEmpresas.isEmpty()) {
                empresa = todasEmpresas.get(0);
            } else {
                throw new RuntimeException("Nenhuma empresa no contexto da requisição e nenhuma empresa cadastrada.");
            }
        }

        RecebimentoNf recebimento = new RecebimentoNf();
        recebimento.setEmpresa(empresa);
        recebimento.setChaveAcessoNfe(chave);
        recebimento.setNumeroNfe(numNf);
        recebimento.setDataRecebimento(LocalDateTime.now());
        recebimento.setStatus(RecebimentoNfStatus.PENDENTE);

        // Vincular Ordem de Compra se fornecida
        if (ordemCompraId != null) {
            OrdemCompra oc = ordemCompraRepository.findById(ordemCompraId)
                    .orElseThrow(() -> new RuntimeException("Ordem de Compra não encontrada"));
            recebimento.setOrdemCompra(oc);
            
            // Valida se a OC é do mesmo fornecedor que enviou a NF? Aqui poderiamos comparar o CNPJ
            // Mas para simplificar, setamos o fornecedor baseado na OC.
            recebimento.setFornecedor(oc.getFornecedor());
            
            // Atualiza status da OC para ENTREGUE ou PARCIAL (simplificado aqui para ENTREGUE)
            oc.setStatus(OrdemCompraStatus.RECEBIDA);
            ordemCompraRepository.save(oc);
        } else {
            // Se não tem OC, tenta achar o Fornecedor pelo CNPJ
            // Se não encontrar, cadastra automaticamente usando os dados do emissor da NF.
            Fornecedor fornecedor = fornecedorRepository.findByDocumento(cnpjFornecedor)
                    .orElseGet(() -> {
                        Fornecedor novoFornecedor = new Fornecedor();
                        novoFornecedor.setEmpresa(empresa); // empresa deve estar disponível aqui
                        novoFornecedor.setDocumento(cnpjFornecedor);
                        
                        String nomeEmissor = inf.emit != null && inf.emit.xNome != null && !inf.emit.xNome.trim().isEmpty() 
                                ? inf.emit.xNome 
                                : "Fornecedor " + cnpjFornecedor;
                        
                        novoFornecedor.setNome(nomeEmissor);
                        novoFornecedor.setRazaoSocial(nomeEmissor);
                        novoFornecedor.setTipoPessoa(cnpjFornecedor != null && cnpjFornecedor.length() == 11 ? "PF" : "PJ");
                        novoFornecedor.setStatus("ATIVO");
                        novoFornecedor.setCategoriaFornecedor("NFE_AUTO_CADASTRO");
                        
                        return fornecedorRepository.save(novoFornecedor);
                    });
            recebimento.setFornecedor(fornecedor);
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        if (inf.det != null) {
            for (NfeProc.Det det : inf.det) {
                if (det.prod != null) {
                    RecebimentoNfItem item = new RecebimentoNfItem();
                    item.setNomeProdutoNf(det.prod.xProd);
                    item.setCodigoProdutoNf(det.prod.cProd);
                    
                    BigDecimal qtd = new BigDecimal(det.prod.qCom);
                    BigDecimal valUn = new BigDecimal(det.prod.vUnCom);
                    BigDecimal valProd = new BigDecimal(det.prod.vProd);
                    
                    item.setQuantidade(qtd);
                    item.setValorUnitario(valUn);
                    item.setValorTotal(valProd);
                    
                    valorTotal = valorTotal.add(valProd);
                    
                    // Inteligência De-Para
                    if (recebimento.getFornecedor() != null && recebimento.getFornecedor().getId() != null) {
                        java.util.Optional<com.erp.inventory.domain.MaterialFornecedor> mapping = 
                            materialFornecedorRepository.findByFornecedorIdAndCodigoFornecedor(
                                recebimento.getFornecedor().getId(), 
                                det.prod.cProd
                            );
                        if (mapping.isPresent()) {
                            item.setMaterial(mapping.get().getMaterial());
                            // Armazena temporariamente o fator de conversão no item? 
                            // RecebimentoNfItem atualmente não tem campo fatorConversao. 
                            // Mas passaremos isso no response de qualquer forma mapeando depois.
                        }
                    }
                    
                    recebimento.addItem(item);
                }
            }
        }
        
        recebimento.setValorTotalNf(valorTotal);
        
        recebimento = recebimentoNfRepository.save(recebimento);
        
        // TODO: Integração com EstoqueMovimentacaoService para dar entrada real no estoque.
        
        return toResponse(recebimento);
    }

    private RecebimentoNfResponse toResponse(RecebimentoNf r) {
        return new RecebimentoNfResponse(
                r.getId(),
                r.getChaveAcessoNfe(),
                r.getNumeroNfe(),
                r.getFornecedor() != null ? r.getFornecedor().getId() : null,
                r.getFornecedor() != null ? r.getFornecedor().getNome() : null,
                r.getOrdemCompra() != null ? r.getOrdemCompra().getId() : null,
                r.getOrdemCompra() != null ? r.getOrdemCompra().getNumeroPedido() : null,
                r.getValorTotalNf(),
                r.getStatus(),
                r.getDataRecebimento(),
                r.getCriadoEm(),
                r.getItens().stream().map(item -> {
                        com.erp.inventory.domain.MaterialFornecedor mapFornecedor = null;
                        if (item.getMaterial() != null && r.getFornecedor() != null) {
                            mapFornecedor = materialFornecedorRepository.findByFornecedorIdAndCodigoFornecedor(
                                    r.getFornecedor().getId(), item.getCodigoProdutoNf()
                            ).orElse(null);
                        }
                        return new RecebimentoNfItemResponse(
                                item.getId(),
                                item.getNomeProdutoNf(),
                                item.getCodigoProdutoNf(),
                                item.getQuantidade(),
                                item.getValorUnitario(),
                                item.getValorTotal(),
                                null,
                                item.getMaterial() != null ? item.getMaterial().getId() : null,
                                item.getMaterial() != null ? item.getMaterial().getNome() : null,
                                item.getMaterial() != null ? item.getMaterial().getCodigo() : null,
                                mapFornecedor != null ? mapFornecedor.getFatorConversao() : null
                        );
                }).collect(Collectors.toList())
        );
    }
    @Transactional
    public void confirmarEntradaEstoque(UUID recebimentoId) {
        RecebimentoNf recebimento = recebimentoNfRepository.findById(recebimentoId)
                .orElseThrow(() -> new RuntimeException("Recebimento não encontrado"));

        if (recebimento.getStatus() == RecebimentoNfStatus.PROCESSADA) {
            throw new RuntimeException("Este recebimento já foi processado e o estoque já foi atualizado.");
        }

        for (RecebimentoNfItem item : recebimento.getItens()) {
            if (item.getMaterial() == null && recebimento.getFornecedor() != null) {
                // Tenta buscar o mapeamento agora, caso o usuário tenha acabado de fazer o De/Para
                java.util.Optional<com.erp.inventory.domain.MaterialFornecedor> mapping = 
                    materialFornecedorRepository.findByFornecedorIdAndCodigoFornecedor(
                        recebimento.getFornecedor().getId(), 
                        item.getCodigoProdutoNf()
                    );
                if (mapping.isPresent()) {
                    item.setMaterial(mapping.get().getMaterial());
                }
            }

            if (item.getMaterial() == null) {
                throw new RuntimeException("O item " + item.getNomeProdutoNf() + " (" + item.getCodigoProdutoNf() + ") não está vinculado a um material interno.");
            }
            
            BigDecimal fatorConversao = BigDecimal.ONE;
            if (recebimento.getFornecedor() != null) {
                java.util.Optional<com.erp.inventory.domain.MaterialFornecedor> mapping = 
                    materialFornecedorRepository.findByFornecedorIdAndCodigoFornecedor(
                        recebimento.getFornecedor().getId(), item.getCodigoProdutoNf());
                if (mapping.isPresent() && mapping.get().getFatorConversao() != null) {
                    fatorConversao = mapping.get().getFatorConversao();
                }
            }
            
            BigDecimal qtdParaEstoque = item.getQuantidade().multiply(fatorConversao);
            
            String referencia = "Entrada NF " + recebimento.getNumeroNfe();
            estoqueMovimentacaoService.registrarMovimentacao(
                    item.getMaterial().getId(), 
                    com.erp.inventory.domain.TipoMovimentacao.ENTRADA, 
                    qtdParaEstoque, 
                    referencia
            );
        }

        recebimento.setStatus(RecebimentoNfStatus.PROCESSADA);
        recebimentoNfRepository.save(recebimento);
    }
}
