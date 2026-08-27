package com.erp.core.domain;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.domain.ProdutoSku;
import com.erp.core.security.UserDetailsImpl;
import com.erp.core.security.UsuarioEmpresa;
import com.erp.core.security.dto.UsuarioCreateDTO;
import com.erp.core.security.dto.UsuarioDTO;
import com.erp.core.security.dto.EmpresaSimpleDTO;
import com.erp.inventory.domain.EstoqueMovimentacao;
import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.domain.Material;
import com.erp.production.domain.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class DomainCoverageTest {

    @Test
    void testEmpresaGettersAndSetters() {
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setEmpresa(empresa);
        assertEquals(empresa, produtoBase.getEmpresa());

        ProdutoSku produtoSku = new ProdutoSku();
        produtoSku.setEmpresa(empresa);
        assertEquals(empresa, produtoSku.getEmpresa());

        Categoria categoria = new Categoria();
        categoria.setEmpresa(empresa);
        assertEquals(empresa, categoria.getEmpresa());

        Cliente cliente = new Cliente();
        cliente.setEmpresa(empresa);
        assertEquals(empresa, cliente.getEmpresa());

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setEmpresa(empresa);
        assertEquals(empresa, fornecedor.getEmpresa());

        Funcionario funcionario = new Funcionario();
        funcionario.setEmpresa(empresa);
        assertEquals(empresa, funcionario.getEmpresa());

        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setEmpresa(empresa);
        assertEquals(empresa, unidadeMedida.getEmpresa());

        EstoqueMovimentacao estoqueMovimentacao = new EstoqueMovimentacao();
        estoqueMovimentacao.setEmpresa(empresa);
        assertEquals(empresa, estoqueMovimentacao.getEmpresa());

        EstoqueProdutoMovimentacao estoqueProdutoMovimentacao = new EstoqueProdutoMovimentacao();
        estoqueProdutoMovimentacao.setEmpresa(empresa);
        assertEquals(empresa, estoqueProdutoMovimentacao.getEmpresa());

        Material material = new Material();
        material.setEmpresa(empresa);
        assertEquals(empresa, material.getEmpresa());

        Apontamento apontamento = new Apontamento();
        apontamento.setEmpresa(empresa);
        assertEquals(empresa, apontamento.getEmpresa());

        Cupom cupom = new Cupom();
        cupom.setEmpresa(empresa);
        assertEquals(empresa, cupom.getEmpresa());

        FichaTecnica fichaTecnica = new FichaTecnica();
        fichaTecnica.setEmpresa(empresa);
        assertEquals(empresa, fichaTecnica.getEmpresa());

        FichaTecnicaMaterial fichaTecnicaMaterial = new FichaTecnicaMaterial();
        fichaTecnicaMaterial.setEmpresa(empresa);
        assertEquals(empresa, fichaTecnicaMaterial.getEmpresa());

        FichaTecnicaOperacao fichaTecnicaOperacao = new FichaTecnicaOperacao();
        fichaTecnicaOperacao.setEmpresa(empresa);
        assertEquals(empresa, fichaTecnicaOperacao.getEmpresa());

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setEmpresa(empresa);
        assertEquals(empresa, ocorrencia.getEmpresa());

        OrdemProducao ordemProducao = new OrdemProducao();
        ordemProducao.setEmpresa(empresa);
        assertEquals(empresa, ordemProducao.getEmpresa());

        OrdemProducaoItem ordemProducaoItem = new OrdemProducaoItem();
        ordemProducaoItem.setEmpresa(empresa);
        assertEquals(empresa, ordemProducaoItem.getEmpresa());

        Pacote pacote = new Pacote();
        pacote.setEmpresa(empresa);
        assertEquals(empresa, pacote.getEmpresa());

        TabelaTempoPadrao tabelaTempoPadrao = new TabelaTempoPadrao();
        tabelaTempoPadrao.setEmpresa(empresa);
        assertEquals(empresa, tabelaTempoPadrao.getEmpresa());
    }

    @Test
    void testSecurityCustomGettersSetters() {
        com.erp.core.security.Usuario usuario = new com.erp.core.security.Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("test");
        usuario.setSenha("test");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        List<String> empresas = new ArrayList<>();
        empresas.add("test-empresa");
        userDetails.setEmpresas(empresas);
        assertEquals(empresas, userDetails.getEmpresas());

        UsuarioEmpresa usuarioEmpresa = new UsuarioEmpresa();
        UUID eId = UUID.randomUUID();
        usuarioEmpresa.setEmpresaId(eId);
        assertEquals(eId, usuarioEmpresa.getEmpresaId());

        UsuarioCreateDTO uc = new UsuarioCreateDTO();
        List<UUID> uuidList = new ArrayList<>();
        uc.setEmpresaIds(uuidList);
        assertEquals(uuidList, uc.getEmpresaIds());

        UsuarioDTO ud = new UsuarioDTO();
        List<EmpresaSimpleDTO> esList = new ArrayList<>();
        ud.setEmpresas(esList);
        assertEquals(esList, ud.getEmpresas());
    }
}
