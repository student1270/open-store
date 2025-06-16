package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.service.ImageService;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ImageService imageService;


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


    @PostMapping("/add-product")
    public String addProduct(@RequestParam("image")MultipartFile image , Model model) throws IOException {
        if (imageService.uploadImage(image)) {
            model.addAttribute("message", "Rasm muvaffaqiyatli yuklandi!");
        } else {
            model.addAttribute("message", "Rasm yuklashda xatolik yuz berdi!");
        }
        return "redirect:/admin/add-product";

    }


}