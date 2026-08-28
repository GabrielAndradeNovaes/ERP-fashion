package com.erp.inventory.controller;

import com.erp.inventory.dto.MaterialRequest;
import com.erp.inventory.dto.MaterialResponse;
import com.erp.inventory.service.MaterialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialControllerTest {

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private MaterialController controller;

    @Test
    void testCreateMaterial() {
        MaterialRequest req = new MaterialRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        MaterialResponse res = new MaterialResponse(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(materialService.createMaterial(req)).thenReturn(res);
        ResponseEntity<MaterialResponse> result = controller.createMaterial(req);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void testGetAllMateriais() {
        List<MaterialResponse> list = Collections.singletonList(new MaterialResponse(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        when(materialService.getAllMateriais()).thenReturn(list);
        ResponseEntity<List<MaterialResponse>> result = controller.getAllMateriais();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testGetMaterialById() {
        UUID id = UUID.randomUUID();
        MaterialResponse res = new MaterialResponse(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(materialService.getMaterialById(id)).thenReturn(res);
        ResponseEntity<MaterialResponse> result = controller.getMaterialById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }
}
