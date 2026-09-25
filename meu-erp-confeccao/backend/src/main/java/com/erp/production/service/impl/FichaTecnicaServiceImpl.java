package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.domain.TipoTrajeto;
import com.erp.production.dto.*;
import com.erp.production.repository.FichaTecnicaOperacaoRepository;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.OrdemProducaoRepository;
import com.erp.production.repository.TabelaTempoPadraoRepository;
import com.erp.production.service.FichaTecnicaService;
import com.erp.production.service.MotorCalculoSamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FichaTecnicaServiceImpl implements FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final ProdutoBaseRepository produtoBaseRepository;
    private final MaterialRepository materialRepository;
    private final TabelaTempoPadraoRepository tabelaTempoPadraoRepository;
    private final FichaTecnicaOperacaoRepository fichaTecnicaOperacaoRepository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final MotorCalculoSamService motorCalculoSamService;

    public FichaTecnicaServiceImpl(FichaTecnicaRepository fichaTecnicaRepository, 
                                   ProdutoBaseRepository produtoBaseRepository, 
                                   MaterialRepository materialRepository,
                                   TabelaTempoPadraoRepository tabelaTempoPadraoRepository,
                                   FichaTecnicaOperacaoRepository fichaTecnicaOperacaoRepository,
                                   OrdemProducaoRepository ordemProducaoRepository,
                                   MotorCalculoSamService motorCalculoSamService) {
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.produtoBaseRepository = produtoBaseRepository;
        this.materialRepository = materialRepository;
        this.tabelaTempoPadraoRepository = tabelaTempoPadraoRepository;
        this.fichaTecnicaOperacaoRepository = fichaTecnicaOperacaoRepository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.motorCalculoSamService = motorCalculoSamService;
    }

    @Override
    @Transactional
    public FichaTecnicaResponse createFichaTecnica(FichaTecnicaRequest request) {
        ProdutoBase produtoBase = produtoBaseRepository.findById(request.produtoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Produto Base não encontrado com ID: " + request.produtoBaseId()));

        FichaTecnica fichaTecnica = new FichaTecnica();
        fichaTecnica.setProdutoBase(produtoBase);
        fichaTecnica.setVersao(request.versao());
        fichaTecnica.setObservacoes(request.observacoes());

        if (request.materiais() != null) {
            for (FichaTecnicaMaterialRequest matReq : request.materiais()) {
                Material material = materialRepository.findById(matReq.materialId())
                        .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com ID: " + matReq.materialId()));

                FichaTecnicaMaterial fctMaterial = new FichaTecnicaMaterial();
                fctMaterial.setMaterial(material);
                fctMaterial.setQuantidade(matReq.quantidade());
                
                fichaTecnica.addMaterial(fctMaterial);
            }
        }

        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar o precoCusto do ProdutoBase
        BigDecimal custoTotal = BigDecimal.ZERO;
        if (saved.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : saved.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotal = custoTotal.add(mat.getQuantidade().multiply(custoUnit));
            }
        }
        produtoBase.setPrecoCusto(custoTotal);
        produtoBaseRepository.save(produtoBase);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichaTecnicaResponse> getFichasPorProduto(UUID produtoBaseId) {
        return fichaTecnicaRepository.findAll().stream()
                .filter(f -> f.getProdutoBase().getId().equals(produtoBaseId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FichaTecnicaResponse getFichaTecnicaById(UUID id) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + id));
        return mapToResponse(fichaTecnica);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse addOperacao(UUID fichaTecnicaId, FichaTecnicaOperacaoRequest request) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        fichaTecnica = verificarEClonarSeNecessario(fichaTecnica);

        Integer qFolhas = request.quantidadeFolhas() != null ? request.quantidadeFolhas() : 0;
        Integer qParadas = request.quantidadeParadas() != null ? request.quantidadeParadas() : 0;

        if (fichaTecnica.getOperacoes().stream().anyMatch(op -> op.getOrdemExecucao().equals(request.ordemExecucao()))) {
            throw new IllegalArgumentException("Já existe uma operação com esta ordem de execução (ordem " + request.ordemExecucao() + ") na Ficha Técnica.");
        }

        FichaTecnicaOperacao operacao = new FichaTecnicaOperacao();
        operacao.setNome(request.nome());
        operacao.setMaquina(request.maquina());
        operacao.setOrdemExecucao(request.ordemExecucao());
        operacao.setQuantidadeFolhas(qFolhas);
        operacao.setQuantidadeParadas(qParadas);
        operacao.setRpmMaquina(request.rpmMaquina());
        operacao.setPontosPorCm(request.pontosPorCm());
        operacao.setComprimentoCosturaCm(request.comprimentoCosturaCm());
        operacao.setTipoTrajeto(request.tipoTrajeto());
        operacao.setDificuldadeTecido(request.dificuldadeTecido());
        operacao.setTempoCalculadoCentesimal(BigDecimal.ZERO); // Default to avoid not-null constraint
        
        if (request.rpmMaquina() != null && request.pontosPorCm() != null && (request.tipoTrajeto() == TipoTrajeto.CICLO_FIXO || request.comprimentoCosturaCm() != null)) {
            BigDecimal comprimento = request.comprimentoCosturaCm() != null ? request.comprimentoCosturaCm() : BigDecimal.ZERO;
            CalculoSamInput samInput = new CalculoSamInput(request.rpmMaquina(), request.pontosPorCm(), comprimento, qFolhas, request.dificuldadeTecido(), request.tipoTrajeto(), request.quantidadeParadas());
            CalculoSamOutput samOutput = motorCalculoSamService.calcularOperacao(samInput);
            operacao.setSamMinutos(samOutput.getSamMinutos());
            operacao.setTempoCalculadoCentesimal(samOutput.getSamMinutos());
            operacao.setQuantidadeParadas(samOutput.getParadasUtilizadas());
        }


        fichaTecnica.addOperacao(operacao);
        
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse updateOperacao(UUID fichaTecnicaId, UUID operacaoId, FichaTecnicaOperacaoRequest request) {
        FichaTecnica fichaTecnicaOriginal = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        FichaTecnicaOperacao operacaoAntiga = fichaTecnicaOperacaoRepository.findById(operacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada com ID: " + operacaoId));

        Integer ordemExecucaoAntiga = operacaoAntiga.getOrdemExecucao();

        FichaTecnica fichaTecnica = verificarEClonarSeNecessario(fichaTecnicaOriginal);

        FichaTecnicaOperacao operacao = fichaTecnica.getOperacoes().stream()
                .filter(op -> op.getOrdemExecucao().equals(ordemExecucaoAntiga))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada no clone da Ficha Técnica"));

        if (!operacao.getFichaTecnica().getId().equals(fichaTecnicaId)) {
            throw new IllegalArgumentException("A operação não pertence a esta Ficha Técnica.");
        }

        if (fichaTecnica.getOperacoes().stream().anyMatch(op -> op.getOrdemExecucao().equals(request.ordemExecucao()) && !op.getId().equals(operacaoId))) {
            throw new IllegalArgumentException("Já existe outra operação com esta ordem de execução (ordem " + request.ordemExecucao() + ") na Ficha Técnica.");
        }

        Integer qFolhas = request.quantidadeFolhas() != null ? request.quantidadeFolhas() : 0;
        Integer qParadas = request.quantidadeParadas() != null ? request.quantidadeParadas() : 0;

        operacao.setNome(request.nome());
        operacao.setMaquina(request.maquina());
        operacao.setOrdemExecucao(request.ordemExecucao());
        operacao.setQuantidadeFolhas(qFolhas);
        operacao.setQuantidadeParadas(qParadas);
        operacao.setRpmMaquina(request.rpmMaquina());
        operacao.setPontosPorCm(request.pontosPorCm());
        operacao.setComprimentoCosturaCm(request.comprimentoCosturaCm());
        operacao.setTipoTrajeto(request.tipoTrajeto());
        operacao.setDificuldadeTecido(request.dificuldadeTecido());

        if (request.rpmMaquina() != null && request.pontosPorCm() != null && (request.tipoTrajeto() == TipoTrajeto.CICLO_FIXO || request.comprimentoCosturaCm() != null)) {
            BigDecimal comprimento = request.comprimentoCosturaCm() != null ? request.comprimentoCosturaCm() : BigDecimal.ZERO;
            CalculoSamInput samInput = new CalculoSamInput(request.rpmMaquina(), request.pontosPorCm(), comprimento, qFolhas, request.dificuldadeTecido(), request.tipoTrajeto(), request.quantidadeParadas());
            CalculoSamOutput samOutput = motorCalculoSamService.calcularOperacao(samInput);
            operacao.setSamMinutos(samOutput.getSamMinutos());
            operacao.setTempoCalculadoCentesimal(samOutput.getSamMinutos());
            operacao.setQuantidadeParadas(samOutput.getParadasUtilizadas());
        }

        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse removeOperacao(UUID fichaTecnicaId, UUID operacaoId) {
        FichaTecnica fichaTecnicaOriginal = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        FichaTecnicaOperacao operacaoAntiga = fichaTecnicaOperacaoRepository.findById(operacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada com ID: " + operacaoId));

        Integer ordemExecucaoAntiga = operacaoAntiga.getOrdemExecucao();

        FichaTecnica fichaTecnica = verificarEClonarSeNecessario(fichaTecnicaOriginal);

        FichaTecnicaOperacao operacao = fichaTecnica.getOperacoes().stream()
                .filter(op -> op.getOrdemExecucao().equals(ordemExecucaoAntiga))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada no clone da Ficha Técnica"));

        if (!operacao.getFichaTecnica().getId().equals(fichaTecnicaId)) {
            throw new IllegalArgumentException("A operação não pertence a esta Ficha Técnica.");
        }

        fichaTecnica.removeOperacao(operacao);
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse addMaterial(UUID fichaTecnicaId, com.erp.production.dto.FichaTecnicaMaterialRequest request) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        fichaTecnica = verificarEClonarSeNecessario(fichaTecnica);

        Material material = materialRepository.findById(request.materialId())
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com ID: " + request.materialId()));

        FichaTecnicaMaterial fctMaterial = new FichaTecnicaMaterial();
        fctMaterial.setMaterial(material);
        fctMaterial.setQuantidade(request.quantidade());

        fichaTecnica.addMaterial(fctMaterial);
        
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar precoCusto do ProdutoBase
        atualizarPrecoCustoProdutoBase(saved);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse removeMaterial(UUID fichaTecnicaId, UUID materialId) {
        FichaTecnica fichaTecnicaOriginal = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        FichaTecnicaMaterial materialAntigo = fichaTecnicaOriginal.getMateriais().stream()
                .filter(m -> m.getId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado na Ficha Técnica"));

        UUID materialRealId = materialAntigo.getMaterial().getId();

        FichaTecnica fichaTecnica = verificarEClonarSeNecessario(fichaTecnicaOriginal);

        FichaTecnicaMaterial materialToRemove = fichaTecnica.getMateriais().stream()
                .filter(m -> m.getMaterial().getId().equals(materialRealId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado no clone da Ficha Técnica"));

        fichaTecnica.removeMaterial(materialToRemove);
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar precoCusto do ProdutoBase
        atualizarPrecoCustoProdutoBase(saved);

        return mapToResponse(saved);
    }

    private void atualizarPrecoCustoProdutoBase(FichaTecnica ficha) {
        ProdutoBase produto = ficha.getProdutoBase();
        produto.setFichaTecnica(ficha); // Ensure this is set to active

        BigDecimal custoTotal = BigDecimal.ZERO;
        if (ficha.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : ficha.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotal = custoTotal.add(mat.getQuantidade().multiply(custoUnit));
            }
        }
        produto.setPrecoCusto(custoTotal);
        produtoBaseRepository.save(produto);
    }

    private FichaTecnicaResponse mapToResponse(FichaTecnica fichaTecnica) {
        List<FichaTecnicaMaterialResponse> materiais = new ArrayList<>();
        BigDecimal custoTotalMateriais = BigDecimal.ZERO;
        
        if (fichaTecnica.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : fichaTecnica.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotalMateriais = custoTotalMateriais.add(mat.getQuantidade().multiply(custoUnit));
                
                materiais.add(new FichaTecnicaMaterialResponse(
                        mat.getId(),
                        mat.getMaterial().getId(),
                        mat.getMaterial().getNome(),
                        mat.getMaterial().getUnidadeMedida() != null ? mat.getMaterial().getUnidadeMedida().getNome() : null,
                        mat.getQuantidade()
                ));
            }
        }

        List<FichaTecnicaOperacaoResponse> operacoes = new ArrayList<>();
        if (fichaTecnica.getOperacoes() != null) {
            operacoes = fichaTecnica.getOperacoes().stream()
                    .map(op -> new FichaTecnicaOperacaoResponse(
                            op.getId(),
                            op.getNome(),
                            op.getMaquina(),
                            op.getOrdemExecucao(),
                            op.getQuantidadeFolhas(),
                            op.getQuantidadeParadas(),
                            op.getRpmMaquina(),
                            op.getPontosPorCm(),
                            op.getComprimentoCosturaCm(),
                            op.getTipoTrajeto(),
                            op.getDificuldadeTecido(),
                            op.getSamMinutos(),
                            op.getTempoCalculadoCentesimal()
                    )).collect(Collectors.toList());
        }

        BigDecimal tempoPadraoTotal = fichaTecnica.getTempoPadraoTotalCentesimal();
        if (tempoPadraoTotal == null) tempoPadraoTotal = BigDecimal.ZERO;

        return new FichaTecnicaResponse(
                fichaTecnica.getId(),
                fichaTecnica.getProdutoBase().getId(),
                fichaTecnica.getProdutoBase().getNome(),
                fichaTecnica.getVersao(),
                fichaTecnica.getObservacoes(),
                tempoPadraoTotal,
                custoTotalMateriais,
                materiais,
                operacoes
        );
    }
    private FichaTecnica verificarEClonarSeNecessario(FichaTecnica ficha) {
        boolean emUso = ordemProducaoRepository.existsByFichaTecnicaId(ficha.getId());
        if (!emUso) {
            return ficha;
        }

        ficha.setAtiva(false);
        fichaTecnicaRepository.save(ficha);

        FichaTecnica novaFicha = new FichaTecnica();
        novaFicha.setProdutoBase(ficha.getProdutoBase());
        
        String vAtual = ficha.getVersao() != null ? ficha.getVersao().toUpperCase() : "V1";
        if (vAtual.startsWith("V")) {
            try {
                int vNum = Integer.parseInt(vAtual.substring(1));
                novaFicha.setVersao("V" + (vNum + 1));
            } catch(Exception e) {
                novaFicha.setVersao(vAtual + "-NOVA");
            }
        } else {
            novaFicha.setVersao(vAtual + "-NOVA");
        }
        
        novaFicha.setObservacoes(ficha.getObservacoes());
        novaFicha.setAtiva(true);

        if (ficha.getMateriais() != null) {
            for(FichaTecnicaMaterial fm : ficha.getMateriais()) {
                FichaTecnicaMaterial novoMat = new FichaTecnicaMaterial();
                novoMat.setMaterial(fm.getMaterial());
                novoMat.setQuantidade(fm.getQuantidade());
                novaFicha.addMaterial(novoMat);
            }
        }

        if (ficha.getOperacoes() != null) {
            for(FichaTecnicaOperacao fo : ficha.getOperacoes()) {
                FichaTecnicaOperacao novaOp = new FichaTecnicaOperacao();
                novaOp.setNome(fo.getNome());
                novaOp.setMaquina(fo.getMaquina());
                novaOp.setOrdemExecucao(fo.getOrdemExecucao());
                novaOp.setQuantidadeFolhas(fo.getQuantidadeFolhas());
                novaOp.setQuantidadeParadas(fo.getQuantidadeParadas());
                novaOp.setRpmMaquina(fo.getRpmMaquina());
                novaOp.setPontosPorCm(fo.getPontosPorCm());
                novaOp.setComprimentoCosturaCm(fo.getComprimentoCosturaCm());
                novaOp.setTipoTrajeto(fo.getTipoTrajeto());
                novaOp.setDificuldadeTecido(fo.getDificuldadeTecido());
                novaOp.setSamMinutos(fo.getSamMinutos());
                novaOp.setTempoCalculadoCentesimal(fo.getTempoCalculadoCentesimal());
                novaFicha.addOperacao(novaOp);
            }
        }

        return fichaTecnicaRepository.save(novaFicha);
    }
}
