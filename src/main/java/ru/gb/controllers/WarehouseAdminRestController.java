package ru.gb.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.service.impl.AdminDetails;
import ru.gb.model.Admin;

@RestController
@RequestMapping("/api/warehouse-admin")
public class WarehouseAdminRestController {

    @GetMapping
    public Admin getWarehouseAdmin(Authentication authentication) {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        return adminDetails.getAdmin();
    }
}