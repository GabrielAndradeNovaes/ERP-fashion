package com.erp.inventory.service;

import com.erp.inventory.dto.MaterialRequest;
import com.erp.inventory.dto.MaterialResponse;

import java.util.List;
import java.util.UUID;

public interface MaterialService {
    MaterialResponse createMaterial(MaterialRequest request);
    List<MaterialResponse> getAllMateriais();
    MaterialResponse getMaterialById(UUID id);
}
