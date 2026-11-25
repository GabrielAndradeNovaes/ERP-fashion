package com.erp.procurement.controller;

import com.erp.procurement.dto.RecebimentoNfResponse;
import com.erp.procurement.service.RecebimentoNfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/recebimentos-nf")
public class RecebimentoNfController {

    private static final Logger log = LoggerFactory.getLogger(RecebimentoNfController.class);

    private final RecebimentoNfService recebimentoNfService;

    public RecebimentoNfController(RecebimentoNfService recebimentoNfService) {
        this.recebimentoNfService = recebimentoNfService;
    }

    @GetMapping
    public ResponseEntity<Page<RecebimentoNfResponse>> listar(
            @RequestParam(required = false) String chaveNfe,
            Pageable pageable) {
        return ResponseEntity.ok(recebimentoNfService.listar(chaveNfe, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecebimentoNfResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(recebimentoNfService.buscarPorId(id));
    }

    @PostMapping("/upload-xml")
    public ResponseEntity<?> uploadXml(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) UUID ordemCompraId) {
        try {
            RecebimentoNfResponse response = recebimentoNfService.processarXmlNfe(file, ordemCompraId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao processar XML de NF-e: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado ao processar XML de NF-e", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Erro ao processar XML da NF-e."));
        }
    }

    @PostMapping("/{id}/confirmar-estoque")
    public ResponseEntity<?> confirmarEstoque(@PathVariable UUID id) {
        try {
            recebimentoNfService.confirmarEntradaEstoque(id);
            return ResponseEntity.ok(Map.of("message", "Estoque atualizado com sucesso"));
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao confirmar estoque do recebimento {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado ao confirmar estoque do recebimento {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Erro ao confirmar entrada no estoque."));
        }
    }
}
