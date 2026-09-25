package com.erp.procurement.controller;

import com.erp.procurement.domain.OrdemCompraStatus;
import com.erp.procurement.dto.OrdemCompraRequest;
import com.erp.procurement.dto.OrdemCompraResponse;
import com.erp.procurement.service.OrdemCompraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/ordens-compra")
@CrossOrigin(origins = "*")
public class OrdemCompraController {

    private final OrdemCompraService ordemCompraService;

    public OrdemCompraController(OrdemCompraService ordemCompraService) {
        this.ordemCompraService = ordemCompraService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdemCompraResponse>> listar(
            @RequestParam(required = false) String numeroPedido,
            Pageable pageable) {
        return ResponseEntity.ok(ordemCompraService.listar(numeroPedido, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemCompraResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemCompraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdemCompraResponse> criar(@RequestBody OrdemCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemCompraService.criar(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemCompraResponse> atualizarStatus(
            @PathVariable UUID id, 
            @RequestParam OrdemCompraStatus status) {
        return ResponseEntity.ok(ordemCompraService.atualizarStatus(id, status));
    }
}
