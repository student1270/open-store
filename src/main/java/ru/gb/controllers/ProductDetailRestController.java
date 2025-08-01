package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Product;
import ru.gb.model.Review;
import ru.gb.service.ProductService;
import ru.gb.service.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductDetailRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.findProductsById(productId);
            if (product == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Mahsulot topilmadi"));
            }

            List<Review> latestReviews = reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(productId)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("productDetailPage", product);
            response.put("latestTwoReviews", latestReviews);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ichki server xatosi"));
        }
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> findReviewsPage(
            @PathVariable Long productId,
            @RequestParam(value = "feedbackId", required = false) Long feedbackId) {
        try {
            List<Review> reviews = reviewService.findReviewsByProductIdDesc(productId)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("productId", productId);

            if (feedbackId != null) {
                response.put("highlightFeedbackId", feedbackId);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ichki server xatosi"));
        }
    }
}