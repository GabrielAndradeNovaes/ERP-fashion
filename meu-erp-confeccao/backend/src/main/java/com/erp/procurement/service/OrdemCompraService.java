package com.erp.procurement.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.procurement.domain.OrdemCompra;
import com.erp.procurement.domain.OrdemCompraItem;
import com.erp.procurement.domain.OrdemCompraStatus;
import com.erp.procurement.dto.OrdemCompraRequest;
import com.erp.procurement.dto.OrdemCompraResponse;
import com.erp.procurement.dto.OrdemCompraItemResponse;
import com.erp.procurement.repository.OrdemCompraRepository;
import com.erp.core.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdemCompraService {

    private final OrdemCompraRepository ordemCompraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EmpresaRepository empresaRepository;
    private final MaterialRepository materialRepository;

    public OrdemCompraService(OrdemCompraRepository ordemCompraRepository, 
                              FornecedorRepository fornecedorRepository, 
                              EmpresaRepository empresaRepository, 
                              MaterialRepository materialRepository) {
        this.ordemCompraRepository = ordemCompraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.empresaRepository = empresaRepository;
        this.materialRepository = materialRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrdemCompraResponse> listar(String numeroPedido, Pageable pageable) {
        Page<OrdemCompra> ordens;
        if (numeroPedido != null && !numeroPedido.isEmpty()) {
            ordens = ordemCompraRepository.findByNumeroPedidoContainingIgnoreCase(numeroPedido, pageable);
        } else {
            ordens = ordemCompraRepository.findAll(pageable);
        }
        return ordens.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrdemCompraResponse buscarPorId(UUID id) {
        OrdemCompra oc = ordemCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Compra não encontrada"));
        return toResponse(oc);
    }

    @Transactional
    public OrdemCompraResponse criar(OrdemCompraRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(request.fornecedorId())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        OrdemCompra oc = new OrdemCompra();
        oc.setFornecedor(fornecedor);
        oc.setNumeroPedido(request.numeroPedido());
        oc.setDataPrevisaoEntrega(request.dataPrevisaoEntrega());
        oc.setObservacoes(request.observacoes());

        // A Empresa normalmente viria do contexto injetado pelo JwtAuthFilter
        java.util.List<UUID> empresas = com.erp.core.tenant.EmpresaContext.getEmpresas();
        if (empresas != null && !empresas.isEmpty()) {
            Empresa empresa = empresaRepository.findById(empresas.get(0))
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada no banco"));
            oc.setEmpresa(empresa);
        } else {
            throw new RuntimeException("Nenhuma empresa no contexto da requisição");
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        if (request.itens() != null) {
            for (var itemReq : request.itens()) {
                Material material = materialRepository.findById(itemReq.materialId())
                        .orElseThrow(() -> new RuntimeException("Material não encontrado: " + itemReq.materialId()));

                OrdemCompraItem item = new OrdemCompraItem();
                item.setMaterial(material);
                item.setQuantidadeSolicitada(itemReq.quantidadeSolicitada());
                item.setPrecoUnitario(itemReq.precoUnitario());
                
                BigDecimal totalItem = itemReq.quantidadeSolicitada().multiply(itemReq.precoUnitario());
                valorTotal = valorTotal.add(totalItem);

                oc.addItem(item);
            }
        }
        
        oc.setValorTotal(valorTotal);

        oc = ordemCompraRepository.save(oc);
        return toResponse(oc);
    }

    @Transactional
    public OrdemCompraResponse atualizarStatus(UUID id, OrdemCompraStatus novoStatus) {
        OrdemCompra oc = ordemCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Compra não encontrada"));
        
        oc.setStatus(novoStatus);
        oc = ordemCompraRepository.save(oc);
        return toResponse(oc);
    }

    private OrdemCompraResponse toResponse(OrdemCompra oc) {
        return new OrdemCompraResponse(
                oc.getId(),
                oc.getFornecedor().getId(),
                oc.getFornecedor().getNome(),
                oc.getNumeroPedido(),
                oc.getDataEmissao(),
                oc.getDataPrevisaoEntrega(),
                oc.getStatus(),
                oc.getValorTotal(),
                oc.getObservacoes(),
                oc.getCriadoEm(),
                oc.getItens().stream().map(item -> new OrdemCompraItemResponse(
                        item.getId(),
                        item.getMaterial().getId(),
                        item.getMaterial().getNome(),
                        item.getQuantidadeSolicitada(),
                        item.getQuantidadeRecebida(),
                        item.getPrecoUnitario(),
                        item.getValorTotal()
                )).collect(Collectors.toList())
        );
    }
}
