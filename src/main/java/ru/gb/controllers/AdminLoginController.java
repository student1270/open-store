package ru.gb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AdminLoginController {

    @GetMapping("/api/admin-login")
    public Map<String, String> adminLoginPage() {
        return Map.of("message", "Admin login page available");
    }
}

//package ru.gb.controllers;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class AdminLoginController {
//
//    @GetMapping("/admin-login")
//    public String adminLoginPage() {
//        return "admin-login";
//    }
//}
