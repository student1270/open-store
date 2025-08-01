


package ru.gb.controllers;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.gb.service.impl.AdminDetails;

@Controller
public class WarehouseAdminController {

    @GetMapping("/warehouse-admin")
    public String warehouseAdminPage(Model model, Authentication authentication) {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        model.addAttribute("admin", adminDetails.getAdmin());
        return "warehouse-admin";
    }

}

