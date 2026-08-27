package com.erp.core.controller;

import com.erp.core.dto.DashboardResumoDTO;
import com.erp.core.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController controller;

    @Test
    void testGetResumo() {
        DashboardResumoDTO dto = new DashboardResumoDTO(1, 2, 3, 4);
        when(dashboardService.getResumo()).thenReturn(dto);

        ResponseEntity<DashboardResumoDTO> result = controller.getResumo();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dto, result.getBody());
    }
}
