package ru.gb.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.service.impl.AdminDetails;
import ru.gb.model.Admin;

@RestController
@RequestMapping("/api/warehouse-admin")
public class WarehouseAdminController {

    @GetMapping
    public Admin getWarehouseAdmin(Authentication authentication) {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        return adminDetails.getAdmin();
    }
}


//package ru.gb.controllers;
//
//
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import ru.gb.service.impl.AdminDetails;
//
//@Controller
//public class WarehouseAdminController {
//
//    @GetMapping("/warehouse-admin")
//    public String warehouseAdminPage(Model model, Authentication authentication) {
//        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
//        model.addAttribute("admin", adminDetails.getAdmin());
//        return "warehouse-admin";
//    }
//
//}
//
