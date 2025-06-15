package ru.gb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String showAdminPage(Model model) {
        model.addAttribute("title", "Admin Paneli");
        return "admin";
    }
        @GetMapping("/add-product")
        public String showAddProductPage (Model model){
            model.addAttribute("title", "Mahsulot Qo‘shish");
            return "add-product";
        }

}