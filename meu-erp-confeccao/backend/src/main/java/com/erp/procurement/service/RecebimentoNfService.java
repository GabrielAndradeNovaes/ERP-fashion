package com.erp.procurement.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.core.tenant.EmpresaContext;
import com.erp.inventory.domain.MaterialFornecedor;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.MaterialFornecedorRepository;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.procurement.domain.*;
import com.erp.procurement.dto.RecebimentoNfItemResponse;
import com.erp.procurement.dto.RecebimentoNfResponse;
import com.erp.procurement.dto.xml.NfeProc;
import com.erp.procurement.repository.OrdemCompraRepository;
import com.erp.procurement.repository.RecebimentoNfRepository;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecebimentoNfService {

    private static final Logger log = LoggerFactory.getLogger(RecebimentoNfService.class);
    private static final Set<String> REJECTED_NFE_STATUSES = Set.of("101", "110", "301", "302", "303");

    private final RecebimentoNfRepository recebimentoNfRepository;
    private final OrdemCompraRepository ordemCompraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EmpresaRepository empresaRepository;
    private final MaterialFornecedorRepository materialFornecedorRepository;
    private final EstoqueMovimentacaoService estoqueMovimentacaoService;
    private final XmlMapper xmlMapper;

    public RecebimentoNfService(RecebimentoNfRepository recebimentoNfRepository,
                                OrdemCompraRepository ordemCompraRepository,
                                FornecedorRepository fornecedorRepository,
                                EmpresaRepository empresaRepository,
                                MaterialFornecedorRepository materialFornecedorRepository,
                                EstoqueMovimentacaoService estoqueMovimentacaoService) {
        this.recebimentoNfRepository = recebimentoNfRepository;
        this.ordemCompraRepository = ordemCompraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.empresaRepository = empresaRepository;
        this.materialFornecedorRepository = materialFornecedorRepository;
        this.estoqueMovimentacaoService = estoqueMovimentacaoService;
        this.xmlMapper = createSecureXmlMapper();
    }

    private static XmlMapper createSecureXmlMapper() {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return new XmlMapper(XmlFactory.builder().xmlInputFactory(xmlInputFactory).build());
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
        if (xmlFile == null || xmlFile.isEmpty()) {
            throw new IllegalArgumentException("Arquivo XML não enviado ou está vazio.");
        }

        String xmlConteudo = new String(xmlFile.getBytes(), StandardCharsets.UTF_8).trim();
        if (xmlConteudo.isEmpty() || !xmlConteudo.startsWith("<")) {
            throw new IllegalArgumentException("O arquivo enviado não é um XML válido.");
        }

        NfeProc nfeProc = parseNfeXml(xmlConteudo);
        if (nfeProc.nfe == null || nfeProc.nfe.infNFe == null) {
            throw new IllegalArgumentException("Formato de XML NFe inválido ou não suportado.");
        }

        // Valida status do protocolo de autorização (rejeita NF cancelada ou denegada)
        if (nfeProc.protNFe != null && nfeProc.protNFe.infProt != null && nfeProc.protNFe.infProt.cStat != null) {
            String cStat = nfeProc.protNFe.infProt.cStat.trim();
            if (REJECTED_NFE_STATUSES.contains(cStat)) {
                String motivo = nfeProc.protNFe.infProt.xMotivo != null ? nfeProc.protNFe.infProt.xMotivo : "Status " + cStat;
                throw new IllegalArgumentException("Nota Fiscal não autorizada para entrada (" + cStat + " - " + motivo + ").");
            }
        }

        NfeProc.InfNFe inf = nfeProc.nfe.infNFe;
        String chave = extractChaveAcesso(inf, nfeProc.protNFe);
        if (chave.isEmpty()) {
            throw new IllegalArgumentException("Chave de acesso da NF-e não encontrada no XML.");
        }

        String numNf = inf.ide != null && inf.ide.nNF != null ? inf.ide.nNF.trim() : "";
        String docFornecedor = extractDocumento(inf.emit != null ? inf.emit.cnpj : null, inf.emit != null ? inf.emit.cpf : null);

        if (recebimentoNfRepository.findByChaveAcessoNfe(chave).isPresent()) {
            throw new IllegalArgumentException("Nota Fiscal com chave " + chave + " já foi recebida.");
        }

        Empresa empresa = resolveEmpresa();

        // Alerta caso o CNPJ destinatário da NF-e difira da empresa ativa (sem bloquear testes/homologação)
        if (inf.dest != null && empresa.getCnpj() != null && !empresa.getCnpj().isBlank()) {
            String docDest = extractDocumento(inf.dest.cnpj, inf.dest.cpf);
            String cnpjEmpresa = empresa.getCnpj().replaceAll("\\D", "");
            if (!docDest.isEmpty() && !cnpjEmpresa.isEmpty() && !docDest.equals(cnpjEmpresa)) {
                log.warn("NF-e {} emitida para destinatário {} diferente do CNPJ da empresa {}", numNf, docDest, cnpjEmpresa);
            }
        }

        RecebimentoNf recebimento = new RecebimentoNf();
        recebimento.setEmpresa(empresa);
        recebimento.setChaveAcessoNfe(chave);
        recebimento.setNumeroNfe(numNf);
        recebimento.setDataEmissao(parseDataEmissao(inf.ide != null ? inf.ide.dhEmi : null));
        recebimento.setDataRecebimento(LocalDateTime.now());
        recebimento.setXmlConteudo(xmlConteudo);
        recebimento.setStatus(RecebimentoNfStatus.PENDENTE);

        // Vincular Ordem de Compra se fornecida (status da OC só passa para RECEBIDA na confirmação do estoque)
        if (ordemCompraId != null) {
            OrdemCompra oc = ordemCompraRepository.findById(ordemCompraId)
                    .orElseThrow(() -> new IllegalArgumentException("Ordem de Compra não encontrada"));
            recebimento.setOrdemCompra(oc);
            recebimento.setFornecedor(oc.getFornecedor());
        } else {
            if (docFornecedor.isEmpty()) {
                throw new IllegalArgumentException("CNPJ/CPF do emissor não encontrado no XML da NF-e.");
            }
            Fornecedor fornecedor = fornecedorRepository.findByDocumento(docFornecedor)
                    .orElseGet(() -> {
                        Fornecedor novoFornecedor = new Fornecedor();
                        novoFornecedor.setEmpresa(empresa);
                        novoFornecedor.setDocumento(docFornecedor);

                        String nomeEmissor = inf.emit != null && inf.emit.xNome != null && !inf.emit.xNome.trim().isEmpty()
                                ? inf.emit.xNome.trim()
                                : "Fornecedor " + docFornecedor;

                        novoFornecedor.setNome(nomeEmissor);
                        novoFornecedor.setRazaoSocial(nomeEmissor);
                        novoFornecedor.setTipoPessoa(docFornecedor.length() == 11 ? "PF" : "PJ");
                        novoFornecedor.setStatus("ATIVO");
                        novoFornecedor.setCategoriaFornecedor("NFE_AUTO_CADASTRO");

                        return fornecedorRepository.save(novoFornecedor);
                    });
            recebimento.setFornecedor(fornecedor);
        }

        // Pré-carrega todos os mapeamentos De/Para do fornecedor em 1 única consulta (evita N+1)
        Map<String, MaterialFornecedor> mapeamentosPorCodigo = loadMapeamentosFornecedor(recebimento.getFornecedor());

        BigDecimal somaItens = BigDecimal.ZERO;

        if (inf.det != null) {
            for (NfeProc.Det det : inf.det) {
                if (det != null && det.prod != null) {
                    RecebimentoNfItem item = new RecebimentoNfItem();
                    item.setNomeProdutoNf(det.prod.xProd);
                    item.setCodigoProdutoNf(det.prod.cProd);
                    item.setNcmNf(det.prod.ncm);

                    BigDecimal qtd = parseBigDecimal(det.prod.qCom, "quantidade (qCom)");
                    BigDecimal valUn = parseBigDecimal(det.prod.vUnCom, "valor unitário (vUnCom)");
                    BigDecimal valProd = det.prod.vProd != null && !det.prod.vProd.isBlank()
                            ? parseBigDecimal(det.prod.vProd, "valor do produto (vProd)")
                            : qtd.multiply(valUn);

                    item.setQuantidade(qtd);
                    item.setValorUnitario(valUn);
                    item.setValorTotal(valProd);

                    somaItens = somaItens.add(valProd);

                    if (det.prod.cProd != null) {
                        MaterialFornecedor mapping = mapeamentosPorCodigo.get(det.prod.cProd);
                        if (mapping != null) {
                            item.setMaterial(mapping.getMaterial());
                        }
                    }

                    recebimento.addItem(item);
                }
            }
        }

        // Prioriza o valor total oficial da NF (<ICMSTot><vNF>), com fallback para soma dos itens
        BigDecimal valorTotalNf = (inf.total != null && inf.total.icmsTot != null && inf.total.icmsTot.vNF != null && !inf.total.icmsTot.vNF.isBlank())
                ? parseBigDecimal(inf.total.icmsTot.vNF, "valor total da NF (vNF)")
                : somaItens;
        recebimento.setValorTotalNf(valorTotalNf);

        recebimento = recebimentoNfRepository.save(recebimento);

        return toResponse(recebimento, mapeamentosPorCodigo);
    }

    private NfeProc parseNfeXml(String xmlConteudo) throws Exception {
        NfeProc nfeProc = xmlMapper.readValue(xmlConteudo, NfeProc.class);
        if (nfeProc.nfe != null && nfeProc.nfe.infNFe != null) {
            return nfeProc;
        }
        // Fallback para XML raiz <NFe> sem envelope <nfeProc>
        NfeProc.NFe standaloneNfe = xmlMapper.readValue(xmlConteudo, NfeProc.NFe.class);
        if (standaloneNfe != null && standaloneNfe.infNFe != null) {
            NfeProc wrapper = new NfeProc();
            wrapper.nfe = standaloneNfe;
            return wrapper;
        }
        return nfeProc;
    }

    private String extractChaveAcesso(NfeProc.InfNFe inf, NfeProc.ProtNFe protNFe) {
        if (inf.id != null && !inf.id.isBlank()) {
            return inf.id.trim().replace("NFe", "");
        }
        if (protNFe != null && protNFe.infProt != null && protNFe.infProt.chNFe != null) {
            return protNFe.infProt.chNFe.trim();
        }
        return "";
    }

    private String extractDocumento(String cnpj, String cpf) {
        String raw = (cnpj != null && !cnpj.isBlank()) ? cnpj : (cpf != null ? cpf : "");
        return raw.replaceAll("\\D", "");
    }

    private LocalDateTime parseDataEmissao(String dhEmi) {
        if (dhEmi == null || dhEmi.isBlank()) {
            return null;
        }
        String trimmed = dhEmi.trim();
        try {
            return OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(trimmed);
            } catch (DateTimeParseException ignored2) {
                try {
                    return LocalDate.parse(trimmed).atStartOfDay();
                } catch (DateTimeParseException ex) {
                    log.warn("Não foi possível interpretar data de emissão (dhEmi): {}", dhEmi);
                    return null;
                }
            }
        }
    }

    private BigDecimal parseBigDecimal(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatório ausente no XML: " + fieldName);
        }
        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor numérico inválido no XML para " + fieldName + ": " + value);
        }
    }

    private Empresa resolveEmpresa() {
        List<UUID> empresas = EmpresaContext.getEmpresas();
        if (empresas != null && !empresas.isEmpty()) {
            return empresaRepository.findById(empresas.get(0))
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada no banco"));
        }
        List<Empresa> todasEmpresas = empresaRepository.findAll();
        if (!todasEmpresas.isEmpty()) {
            return todasEmpresas.get(0);
        }
        throw new RuntimeException("Nenhuma empresa no contexto da requisição e nenhuma empresa cadastrada.");
    }

    private Map<String, MaterialFornecedor> loadMapeamentosFornecedor(Fornecedor fornecedor) {
        if (fornecedor == null || fornecedor.getId() == null) {
            return Collections.emptyMap();
        }
        List<MaterialFornecedor> mappings = materialFornecedorRepository.findByFornecedorId(fornecedor.getId());
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyMap();
        }
        return mappings.stream()
                .filter(m -> m.getCodigoFornecedor() != null)
                .collect(Collectors.toMap(
                        MaterialFornecedor::getCodigoFornecedor,
                        Function.identity(),
                        (existing, replacement) -> replacement
                ));
    }

    private RecebimentoNfResponse toResponse(RecebimentoNf r) {
        return toResponse(r, loadMapeamentosFornecedor(r.getFornecedor()));
    }

    private RecebimentoNfResponse toResponse(RecebimentoNf r, Map<String, MaterialFornecedor> mapeamentosPorCodigo) {
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
                    MaterialFornecedor mapFornecedor = item.getCodigoProdutoNf() != null
                            ? mapeamentosPorCodigo.get(item.getCodigoProdutoNf())
                            : null;
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
                .orElseThrow(() -> new IllegalArgumentException("Recebimento não encontrado"));

        if (recebimento.getStatus() == RecebimentoNfStatus.PROCESSADA) {
            throw new IllegalArgumentException("Este recebimento já foi processado e o estoque já foi atualizado.");
        }

        // Carrega mapeamentos do fornecedor em lote (1 consulta em vez de 2N)
        Map<String, MaterialFornecedor> mapeamentosPorCodigo = loadMapeamentosFornecedor(recebimento.getFornecedor());

        for (RecebimentoNfItem item : recebimento.getItens()) {
            MaterialFornecedor mapping = item.getCodigoProdutoNf() != null
                    ? mapeamentosPorCodigo.get(item.getCodigoProdutoNf())
                    : null;

            if (item.getMaterial() == null && mapping != null) {
                item.setMaterial(mapping.getMaterial());
            }

            if (item.getMaterial() == null) {
                throw new IllegalArgumentException("O item " + item.getNomeProdutoNf() + " (" + item.getCodigoProdutoNf() + ") não está vinculado a um material interno.");
            }

            BigDecimal fatorConversao = (mapping != null && mapping.getFatorConversao() != null)
                    ? mapping.getFatorConversao()
                    : BigDecimal.ONE;

            BigDecimal qtdParaEstoque = item.getQuantidade().multiply(fatorConversao);

            String referencia = "Entrada NF " + recebimento.getNumeroNfe();
            estoqueMovimentacaoService.registrarMovimentacao(
                    item.getMaterial().getId(),
                    TipoMovimentacao.ENTRADA,
                    qtdParaEstoque,
                    referencia
            );
        }

        // Se havia uma Ordem de Compra vinculada, marca como RECEBIDA somente na confirmação real do estoque
        if (recebimento.getOrdemCompra() != null) {
            OrdemCompra oc = recebimento.getOrdemCompra();
            oc.setStatus(OrdemCompraStatus.RECEBIDA);
            ordemCompraRepository.save(oc);
        }

        recebimento.setStatus(RecebimentoNfStatus.PROCESSADA);
        recebimentoNfRepository.save(recebimento);
    }
}
