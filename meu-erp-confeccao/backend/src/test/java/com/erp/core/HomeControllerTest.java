package com.erp.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomeControllerTest {

    @Test
    void testHome() {
        HomeController controller = new HomeController();
        String result = controller.home();
        assertTrue(result.contains("API"));
    }
}
