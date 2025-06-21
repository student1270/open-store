package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "sort", required = false) String sort,
            Model model
    ) {
        try {
            List<Product> products;

            if ("arzon".equals(sort)) {
                products = productRetrievalService.findProductsByCategoryAndPriceAsc(categoryId);
            } else if ("qimmat".equals(sort)) {
                products = productRetrievalService.findProductsByCategoryAndPriceDesc(categoryId);
            } else if ("yangi".equals(sort)) {
                products = productRetrievalService.findProductsByCategory(categoryId);
            } else {
                products = productRetrievalService.findProductsByCategory(categoryId);
            }

            Category category = categoryService.findById(categoryId);

            model.addAttribute("products", products);
            model.addAttribute("category", category);
            model.addAttribute("sort", sort);

        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Noto'g'ri kategoriya ID si.");
            model.addAttribute("products", List.of());
            model.addAttribute("category", null);
            model.addAttribute("sort", null);
        }

        return "product";
    }

    @GetMapping("/category")
    public String redirectToHome() {
        return "redirect:/home";
    }
}
