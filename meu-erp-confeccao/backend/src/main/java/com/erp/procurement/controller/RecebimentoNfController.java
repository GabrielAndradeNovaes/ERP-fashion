package com.erp.procurement.controller;

import com.erp.procurement.dto.RecebimentoNfResponse;
import com.erp.procurement.service.RecebimentoNfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/recebimentos-nf")
@CrossOrigin(origins = "*")
public class RecebimentoNfController {

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
    public ResponseEntity<RecebimentoNfResponse> uploadXml(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) UUID ordemCompraId) {
        try {
            RecebimentoNfResponse response = recebimentoNfService.processarXmlNfe(file, ordemCompraId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
