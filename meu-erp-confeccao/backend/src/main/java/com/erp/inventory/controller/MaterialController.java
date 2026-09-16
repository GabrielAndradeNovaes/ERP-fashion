package com.erp.inventory.controller;

import com.erp.inventory.dto.MaterialRequest;
import com.erp.inventory.dto.MaterialResponse;
import com.erp.inventory.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.erp.core.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api/inventory/materiais")
public class MaterialController {

    private final MaterialService materialService;
    private final SequenceGeneratorService sequenceGeneratorService;

    public MaterialController(MaterialService materialService, SequenceGeneratorService sequenceGeneratorService) {
        this.materialService = materialService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @GetMapping("/next-codigo")
    public ResponseEntity<String> getNextCodigo() {
        return ResponseEntity.ok(sequenceGeneratorService.getNextSequence("materiais", "codigo"));
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(@RequestBody MaterialRequest request) {
        MaterialResponse response = materialService.createMaterial(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMateriais() {
        List<MaterialResponse> responses = materialService.getAllMateriais();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable UUID id) {
        MaterialResponse response = materialService.getMaterialById(id);
        return ResponseEntity.ok(response);
    }
}
