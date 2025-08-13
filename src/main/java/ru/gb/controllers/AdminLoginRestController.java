package ru.gb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminLoginRestController {
    @GetMapping("/api/admin-login")
    public Map<String, String> adminLoginPage() {
        return Map.of("message", "Admin login page available");
    }
}
