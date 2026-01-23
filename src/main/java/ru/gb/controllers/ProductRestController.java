package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Category;
import ru.gb.model.Product;
import ru.gb.service.CategoryService;
import ru.gb.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{categoryId}")
    public Map<String, Object> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        try {
            Sort sortOption = Sort.by(Sort.Direction.DESC, "createdAt");

            if ("arzon".equalsIgnoreCase(sort)) {
                sortOption = Sort.by(Sort.Direction.ASC, "price");
            } else if ("qimmat".equalsIgnoreCase(sort)) {
                sortOption = Sort.by(Sort.Direction.DESC, "price");
            } else if ("yangi".equalsIgnoreCase(sort)) {
                sortOption = Sort.by(Sort.Direction.DESC, "createdAt");
            }

            List<Product> products = productService.findProductsByCategory(categoryId, sortOption);
            Category category = categoryService.findById(categoryId);
            List<Category> categories = categoryService.findAll();

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("category", category);
            result.put("sort", sort);
            result.put("categories", categories);
            return result;

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Noto'g'ri kategoriya ID si.");
            errorResponse.put("products", List.of());
            errorResponse.put("category", null);
            errorResponse.put("sort", null);
            errorResponse.put("categories", categoryService.findAll());
            return errorResponse;
        }
    }


    @GetMapping("/category")
    public Map<String, Object> getAllCategories() {
        return Map.of("categories", categoryService.findAll());
    }
}
