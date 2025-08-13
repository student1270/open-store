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

        AdminLoginRestController controller = new AdminLoginRestController();


        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));


        Map<String, String> response = controller.adminLoginPage();


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