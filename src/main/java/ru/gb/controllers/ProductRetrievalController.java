package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ProductRetrievalService;

import java.util.List;

@Controller
@RequestMapping("/home")
public class ProductRetrievalController {

    @Autowired
    private ProductRetrievalService productRetrievalService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{categoryId}")
    public String getProductsByCategory(@PathVariable Long categoryId, Model model) {
        try {
            List<Product> products = productRetrievalService.findProductsByCategory(categoryId);
            model.addAttribute("products", products);
            Category category = categoryService.findById(categoryId);
            model.addAttribute("category", category);
            if (products.isEmpty()) {
                model.addAttribute("message", "Bu kategoriyada mahsulotlar topilmadi.");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Noto'g'ri kategoriya ID si.");
        }
        return "product";
    }

    @GetMapping("/category")
    public String redirectToHome() {
        return "redirect:/home";
    }
}