package ru.gb.controllers;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminLoginRestControllerTest {

    @Test
    void adminLoginPage_ReturnsCorrectResponse() {
        // 1. Test uchun controller obyektini yaratamiz
        AdminLoginRestController controller = new AdminLoginRestController();

        // 2. Request kontekstini sozlaymiz (agar kerak bo'lsa)
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // 3. Test qilinadigan metodni chaqiramiz
        Map<String, String> response = controller.adminLoginPage();

        // 4. Natijalarni tekshiramiz
        assertEquals(1, response.size());
        assertEquals("Admin login page available", response.get("message"));
    }

    @Test
    void adminLoginPage_ResponseContainsExpectedFields() {
        AdminLoginRestController controller = new AdminLoginRestController();
        Map<String, String> response = controller.adminLoginPage();

        assertTrue(response.containsKey("message"));
        assertEquals("Admin login page available", response.get("message"));
    }
}