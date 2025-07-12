package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Product;
import ru.gb.model.Review;
import ru.gb.service.ProductService;
import ru.gb.service.ReviewService;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product")
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        try {
            // Mahsulotni topamiz
            Product product = productService.findProductsById(productId);
            if (product == null) {
                return "error/404";
            }

            model.addAttribute("productDetailPage", product);

            // So'nggi 2 ta review (null bo'lishi mumkin) — null'larni chiqarib tashlaymiz
            List<Review> latestReviews = reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(productId)
                    .stream()
                    .filter(Objects::nonNull) // null elementlarni olib tashlaymiz
                    .collect(Collectors.toList());

            model.addAttribute("latestTwoReviews", latestReviews);

            return "product-detail-page";
        } catch (Exception e) {
            // Agar xatolik bo‘lsa — 500 sahifasi
            return "error/500";
        }
    }

    @GetMapping("/{productId}/reviews")
    public String findReviewsPage(
            @PathVariable Long productId,
            @RequestParam(value = "feedbackId", required = false) Long feedbackId,
            Model model) {
        // Fikr-mulohazalarni topamiz va null'larni chiqarib tashlaymiz
        List<Review> reviews = reviewService.findReviewsByProductIdDesc(productId)
                .stream()
                .filter(Objects::nonNull) // null elementlarni olib tashlaymiz
                .collect(Collectors.toList());

        model.addAttribute("reviews", reviews);
        model.addAttribute("productId", productId);

        if (feedbackId != null) {
            model.addAttribute("highlightFeedbackId", feedbackId);
        }

        return "product-reviews";
    }
}


/*
@RestController
@RequestMapping("/api/products")
public class ProductDetailController{
    @Autowired
    private ProductService productService;

    @GetMapping("{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId){
        Product product = productService.findProductsById(productId);
        return ResponseEntity.ok(product);
    }

}
*/
