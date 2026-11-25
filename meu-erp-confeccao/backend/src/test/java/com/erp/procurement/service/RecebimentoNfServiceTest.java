package com.erp.procurement.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.MaterialFornecedor;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.MaterialFornecedorRepository;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.procurement.domain.*;
import com.erp.procurement.dto.RecebimentoNfResponse;
import com.erp.procurement.repository.OrdemCompraRepository;
import com.erp.procurement.repository.RecebimentoNfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecebimentoNfServiceTest {

    @Mock
    private RecebimentoNfRepository recebimentoNfRepository;
    @Mock
    private OrdemCompraRepository ordemCompraRepository;
    @Mock
    private FornecedorRepository fornecedorRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private MaterialFornecedorRepository materialFornecedorRepository;
    @Mock
    private EstoqueMovimentacaoService estoqueMovimentacaoService;

    @InjectMocks
    private RecebimentoNfService service;

    private Empresa empresa;
    private Fornecedor fornecedor;

    private static final String VALID_NFE_PROC_XML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc versao="4.00" xmlns="http://www.portalfiscal.inf.br/nfe">
              <NFe>
                <infNFe Id="NFe35231012345678000199550010001234561001234567" versao="4.00">
                  <ide>
                    <nNF>123456</nNF>
                    <dhEmi>2026-10-02T10:00:00-03:00</dhEmi>
                  </ide>
                  <emit>
                    <CNPJ>12345678000199</CNPJ>
                    <xNome>FORNECEDOR DE TECIDOS LTDA</xNome>
                  </emit>
                  <dest>
                    <CNPJ>98765432000111</CNPJ>
                    <xNome>MINHA CONFECCAO</xNome>
                  </dest>
                  <det nItem="1">
                    <prod>
                      <cProd>TEC001</cProd>
                      <xProd>Tecido Algodao Cru - Rolo 50m</xProd>
                      <NCM>52081100</NCM>
                      <uCom>RL</uCom>
                      <qCom>10.0000</qCom>
                      <vUnCom>150.0000</vUnCom>
                      <vProd>1500.00</vProd>
                    </prod>
                  </det>
                  <total>
                    <ICMSTot>
                      <vNF>1550.00</vNF>
                    </ICMSTot>
                  </total>
                </infNFe>
              </NFe>
              <protNFe versao="4.00">
                <infProt>
                  <chNFe>35231012345678000199550010001234561001234567</chNFe>
                  <cStat>100</cStat>
                  <xMotivo>Autorizado o uso da NF-e</xMotivo>
                </infProt>
              </protNFe>
            </nfeProc>
            """;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setCnpj("98765432000111");

        fornecedor = new Fornecedor();
        fornecedor.setId(UUID.randomUUID());
        fornecedor.setDocumento("12345678000199");
        fornecedor.setNome("FORNECEDOR DE TECIDOS LTDA");
    }

    @Test
    void shouldProcessValidNfeProcSavingXmlDataEmissaoNcmAndVnf() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "nfe.xml", "application/xml",
                VALID_NFE_PROC_XML.getBytes(StandardCharsets.UTF_8));

        Material material = new Material();
        material.setId(UUID.randomUUID());
        material.setCodigo("MAT-001");
        material.setNome("Algodão Cru (m)");

        MaterialFornecedor mapping = new MaterialFornecedor();
        mapping.setFornecedor(fornecedor);
        mapping.setMaterial(material);
        mapping.setCodigoFornecedor("TEC001");
        mapping.setFatorConversao(new BigDecimal("50"));

        when(recebimentoNfRepository.findByChaveAcessoNfe("35231012345678000199550010001234561001234567"))
                .thenReturn(Optional.empty());
        when(empresaRepository.findAll()).thenReturn(List.of(empresa));
        when(fornecedorRepository.findByDocumento("12345678000199")).thenReturn(Optional.empty());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);
        when(materialFornecedorRepository.findByFornecedorId(fornecedor.getId())).thenReturn(List.of(mapping));
        when(recebimentoNfRepository.save(any(RecebimentoNf.class))).thenAnswer(inv -> {
            RecebimentoNf saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        RecebimentoNfResponse response = service.processarXmlNfe(file, null);

        assertNotNull(response);
        assertEquals("123456", response.numeroNf());
        assertEquals(new BigDecimal("1550.00"), response.valorTotalNf());
        assertEquals(1, response.itens().size());
        assertEquals(material.getId(), response.itens().get(0).materialId());
        assertEquals(new BigDecimal("50"), response.itens().get(0).fatorConversao());

        ArgumentCaptor<RecebimentoNf> captor = ArgumentCaptor.forClass(RecebimentoNf.class);
        verify(recebimentoNfRepository).save(captor.capture());
        RecebimentoNf persisted = captor.getValue();
        assertNotNull(persisted.getXmlConteudo());
        assertTrue(persisted.getXmlConteudo().contains("<nfeProc"));
        assertNotNull(persisted.getDataEmissao());
        assertEquals(2026, persisted.getDataEmissao().getYear());
        assertEquals("52081100", persisted.getItens().get(0).getNcmNf());
    }

    @Test
    void shouldSupportStandaloneNfeWithoutNfeProcWrapperAndCpfEmitter() throws Exception {
        String standaloneXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <NFe xmlns="http://www.portalfiscal.inf.br/nfe">
                  <infNFe Id="NFe352310111222333445500100000000110000000011" versao="4.00">
                    <ide><nNF>1</nNF></ide>
                    <emit>
                      <CPF>11122233344</CPF>
                      <xNome>PRODUTOR RURAL PF</xNome>
                    </emit>
                    <det nItem="1">
                      <prod>
                        <cProd>P1</cProd>
                        <xProd>Algodão</xProd>
                        <qCom>2</qCom>
                        <vUnCom>10.00</vUnCom>
                        <vProd>20.00</vProd>
                      </prod>
                    </det>
                  </infNFe>
                </NFe>
                """;
        MockMultipartFile file = new MockMultipartFile("file", "nfe.xml", "application/xml",
                standaloneXml.getBytes(StandardCharsets.UTF_8));

        when(recebimentoNfRepository.findByChaveAcessoNfe(any())).thenReturn(Optional.empty());
        when(empresaRepository.findAll()).thenReturn(List.of(empresa));
        when(fornecedorRepository.findByDocumento("11122233344")).thenReturn(Optional.empty());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(inv -> {
            Fornecedor f = inv.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });
        when(materialFornecedorRepository.findByFornecedorId(any())).thenReturn(List.of());
        when(recebimentoNfRepository.save(any(RecebimentoNf.class))).thenAnswer(inv -> inv.getArgument(0));

        RecebimentoNfResponse response = service.processarXmlNfe(file, null);

        assertNotNull(response);
        assertEquals("1", response.numeroNf());
        assertEquals(new BigDecimal("20.00"), response.valorTotalNf());

        ArgumentCaptor<Fornecedor> fornCaptor = ArgumentCaptor.forClass(Fornecedor.class);
        verify(fornecedorRepository).save(fornCaptor.capture());
        assertEquals("PF", fornCaptor.getValue().getTipoPessoa());
        assertEquals("11122233344", fornCaptor.getValue().getDocumento());
    }

    @Test
    void shouldRejectCanceledNfe() {
        String canceledXml = VALID_NFE_PROC_XML.replace("<cStat>100</cStat>", "<cStat>101</cStat>")
                .replace("Autorizado o uso da NF-e", "Cancelamento de NF-e homologado");
        MockMultipartFile file = new MockMultipartFile("file", "cancelada.xml", "application/xml",
                canceledXml.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.processarXmlNfe(file, null));
        assertTrue(ex.getMessage().contains("101"));
    }

    @Test
    void shouldRejectEmptyFileAndDuplicateKey() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.xml", "application/xml", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> service.processarXmlNfe(emptyFile, null));

        MockMultipartFile validFile = new MockMultipartFile("file", "dup.xml", "application/xml",
                VALID_NFE_PROC_XML.getBytes(StandardCharsets.UTF_8));
        when(recebimentoNfRepository.findByChaveAcessoNfe("35231012345678000199550010001234561001234567"))
                .thenReturn(Optional.of(new RecebimentoNf()));

        IllegalArgumentException dupEx = assertThrows(IllegalArgumentException.class,
                () -> service.processarXmlNfe(validFile, null));
        assertTrue(dupEx.getMessage().contains("já foi recebida"));
    }

    @Test
    void shouldConfirmStockWithConversionFactorAndUpdateLinkedOrdemCompra() {
        UUID recebimentoId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();

        Material material = new Material();
        material.setId(materialId);

        OrdemCompra oc = new OrdemCompra();
        oc.setId(UUID.randomUUID());
        oc.setStatus(OrdemCompraStatus.EMITIDA);

        RecebimentoNfItem item = new RecebimentoNfItem();
        item.setCodigoProdutoNf("TEC001");
        item.setNomeProdutoNf("Tecido Rolo 50m");
        item.setQuantidade(new BigDecimal("10"));

        RecebimentoNf recebimento = new RecebimentoNf();
        recebimento.setId(recebimentoId);
        recebimento.setNumeroNfe("123456");
        recebimento.setFornecedor(fornecedor);
        recebimento.setOrdemCompra(oc);
        recebimento.setStatus(RecebimentoNfStatus.PENDENTE);
        recebimento.addItem(item);

        MaterialFornecedor mapping = new MaterialFornecedor();
        mapping.setFornecedor(fornecedor);
        mapping.setMaterial(material);
        mapping.setCodigoFornecedor("TEC001");
        mapping.setFatorConversao(new BigDecimal("50"));

        when(recebimentoNfRepository.findById(recebimentoId)).thenReturn(Optional.of(recebimento));
        when(materialFornecedorRepository.findByFornecedorId(fornecedor.getId())).thenReturn(List.of(mapping));

        service.confirmarEntradaEstoque(recebimentoId);

        verify(estoqueMovimentacaoService, times(1)).registrarMovimentacao(
                eq(materialId),
                eq(TipoMovimentacao.ENTRADA),
                eq(new BigDecimal("500")),
                eq("Entrada NF 123456")
        );
        assertEquals(RecebimentoNfStatus.PROCESSADA, recebimento.getStatus());
        assertEquals(OrdemCompraStatus.RECEBIDA, oc.getStatus());
        verify(ordemCompraRepository, times(1)).save(oc);
        verify(materialFornecedorRepository, times(1)).findByFornecedorId(fornecedor.getId());
    }

    @Test
    void shouldRejectConfirmingAlreadyProcessedRecebimento() {
        UUID recebimentoId = UUID.randomUUID();
        RecebimentoNf recebimento = new RecebimentoNf();
        recebimento.setId(recebimentoId);
        recebimento.setStatus(RecebimentoNfStatus.PROCESSADA);

        when(recebimentoNfRepository.findById(recebimentoId)).thenReturn(Optional.of(recebimento));

        assertThrows(IllegalArgumentException.class, () -> service.confirmarEntradaEstoque(recebimentoId));
        verifyNoInteractions(estoqueMovimentacaoService);
    }
}
