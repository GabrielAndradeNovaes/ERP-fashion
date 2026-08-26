package com.erp.production.controller;

import com.erp.production.dto.ApontamentoRequest;
import com.erp.production.dto.OcorrenciaRequest;
import com.erp.production.dto.ProdutividadeResponse;
import com.erp.production.service.PcpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pcp")
public class BipagemController {

    private final PcpService pcpService;

    public BipagemController(PcpService pcpService) {
        this.pcpService = pcpService;
    }

    @PostMapping("/bipagem")
    public ResponseEntity<Void> biparCupom(@RequestBody ApontamentoRequest request) {
        pcpService.biparCupom(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ocorrencias")
    public ResponseEntity<Void> registrarOcorrencia(@RequestBody OcorrenciaRequest request) {
        pcpService.registrarOcorrencia(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/produtividade")
    public ResponseEntity<ProdutividadeResponse> getProdutividade(
            @RequestParam UUID funcionarioId,
            @RequestParam int mes,
            @RequestParam int ano) {
        
        return ResponseEntity.ok(pcpService.calcularProdutividade(funcionarioId, ano, mes));
    }
}
