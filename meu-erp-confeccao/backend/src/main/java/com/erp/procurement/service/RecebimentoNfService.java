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

    public RecebimentoNfService(RecebimentoNfRepository recebimentoNfRepository,
                                OrdemCompraRepository ordemCompraRepository,
                                FornecedorRepository fornecedorRepository,
                                EmpresaRepository empresaRepository) {
        this.recebimentoNfRepository = recebimentoNfRepository;
        this.ordemCompraRepository = ordemCompraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.empresaRepository = empresaRepository;
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
        if (empresas == null || empresas.isEmpty()) {
            throw new RuntimeException("Nenhuma empresa no contexto da requisição");
        }
        Empresa empresa = empresaRepository.findById(empresas.get(0))
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada no banco"));

        RecebimentoNf recebimento = new RecebimentoNf();
        recebimento.setEmpresa(empresa);
        recebimento.setChaveAcessoNfe(chave);
        recebimento.setNumeroNfe(numNf);
        recebimento.setDataRecebimento(LocalDateTime.now());
        recebimento.setStatus(RecebimentoNfStatus.PROCESSADA); // Ou PENDENTE dependendo da regra

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
            // Como o documento pode estar formatado, isso pode ser frágil, ideal é manter apenas num formato.
            Fornecedor fornecedor = fornecedorRepository.findByDocumento(cnpjFornecedor)
                    .orElseThrow(() -> new RuntimeException("Fornecedor com CNPJ " + cnpjFornecedor + " não encontrado. Cadastre-o primeiro ou vincule a uma OC."));
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
                    
                    // Aqui entraria a inteligência de tentar ligar o item da NF com o item da OC (De-Para)
                    // e consequentemente dar entrada no estoque se o material fosse identificado.
                    
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
                r.getItens().stream().map(item -> new RecebimentoNfItemResponse(
                        item.getId(),
                        item.getNomeProdutoNf(),
                        item.getCodigoProdutoNf(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorTotal(),
                        null,
                        item.getMaterial() != null ? item.getMaterial().getId() : null
                )).collect(Collectors.toList())
        );
    }
}
