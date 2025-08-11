package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ProductService;

import java.util.List;

//@Controller
//@RequestMapping("/home")
public class ProductController {

//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @GetMapping("/category/{categoryId}")
//    public String getProductsByCategory(
//            @PathVariable Long categoryId,
//            @RequestParam(value = "sort", required = false) String sort,
//            Model model
//    ) {
//        try {
//            Sort sortOption = Sort.by(Sort.Direction.DESC, "createdAt"); // default — yangi qo‘shilganlar
//            if ("arzon".equalsIgnoreCase(sort)) {
//                sortOption = Sort.by(Sort.Direction.ASC, "price");
//            } else if ("qimmat".equalsIgnoreCase(sort)) {
//                sortOption = Sort.by(Sort.Direction.DESC, "price");
//            } else if ("yangi".equalsIgnoreCase(sort)) {
//                sortOption = Sort.by(Sort.Direction.DESC, "createdAt");
//            }
//
//            List<Product> products = productService.findProductsByCategory(categoryId, sortOption);
//
//            Category category = categoryService.findById(categoryId);
//            List<Category> categories = categoryService.findAll();
//
//            model.addAttribute("products", products);
//            model.addAttribute("category", category);
//            model.addAttribute("sort", sort);
//            model.addAttribute("categories", categories);
//
//        } catch (IllegalArgumentException e) {
//            model.addAttribute("message", "Noto'g'ri kategoriya ID si.");
//            model.addAttribute("products", List.of());
//            model.addAttribute("category", null);
//            model.addAttribute("sort", null);
//            model.addAttribute("categories", categoryService.findAll());
//        }
//
//        return "product";
//    }
//
//    @GetMapping("/category")
//    public String redirectToHome() {
//        return "redirect:/home";
//    }
}
