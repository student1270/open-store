package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Product;
import ru.gb.service.ProductRetrievalService;
import ru.gb.model.Category;
import ru.gb.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class ProductRetrievalController {

    @Autowired
    private ProductRetrievalService productRetrievalService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        try {
            List<Product> products;

            if ("arzon".equals(sort)) {
                products = productRetrievalService.findProductsByCategoryAndPriceAsc(categoryId);
            } else if ("qimmat".equals(sort)) {
                products = productRetrievalService.findProductsByCategoryAndPriceDesc(categoryId);
            } else {
                products = productRetrievalService.findProductsByCategory(categoryId);
            }

            Category category = categoryService.findById(categoryId);
            List<Category> categories = categoryService.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("category", category);
            response.put("sort", sort);
            response.put("categories", categories);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Noto'g'ri kategoriya ID si.");
            response.put("products", List.of());
            response.put("category", null);
            response.put("sort", null);
            response.put("categories", categoryService.findAll());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> redirectToHome() {
        return ResponseEntity.status(302).header("Location", "/api/home").build();
    }
}


//package ru.gb.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import ru.gb.model.Product;
//import ru.gb.service.ProductRetrievalService;
//import java.util.List;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import ru.gb.model.Category;
//import ru.gb.service.CategoryService;
//
//
//@Controller
//@RequestMapping("/home")
//public class ProductRetrievalController {
//
//    @Autowired
//    private ProductRetrievalService productRetrievalService;
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
//            List<Product> products;
//
//            if ("arzon".equals(sort)) {
//                products = productRetrievalService.findProductsByCategoryAndPriceAsc(categoryId);
//            } else if ("qimmat".equals(sort)) {
//                products = productRetrievalService.findProductsByCategoryAndPriceDesc(categoryId);
//            } else if ("yangi".equals(sort)) {
//                products = productRetrievalService.findProductsByCategory(categoryId);
//            } else {
//                products = productRetrievalService.findProductsByCategory(categoryId);
//            }
//
//            Category category = categoryService.findById(categoryId);
//            List<Category> categories = categoryService.findAll(); // Barcha kategoriyalarni qo‘shish
//
//            model.addAttribute("products", products);
//            model.addAttribute("category", category);
//            model.addAttribute("sort", sort);
//            model.addAttribute("categories", categories); // Kategoriyalar ro‘yxatini qo‘shish
//
//        } catch (IllegalArgumentException e) {
//            model.addAttribute("message", "Noto'g'ri kategoriya ID si.");
//            model.addAttribute("products", List.of());
//            model.addAttribute("category", null);
//            model.addAttribute("sort", null);
//            model.addAttribute("categories", categoryService.findAll()); // Xatolik bo‘lsa ham kategoriyalarni ko‘rsatish
//        }
//
//        return "product";
//    }
//
//    @GetMapping("/category")
//    public String redirectToHome() {
//        return "redirect:/home";
//    }
//}
