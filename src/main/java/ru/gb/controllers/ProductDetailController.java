package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Product;
import ru.gb.model.Review;
import ru.gb.service.ProductService;
import ru.gb.service.ReviewService;

import java.util.Collections;
import java.util.List;
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
        Product product = productService.findProductsById(productId);
        model.addAttribute("productDetailPage", product);

        List<Review> latestReviews = reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(productId)
                .stream()
                .filter(review -> review != null)
                .collect(Collectors.toList());
        model.addAttribute("latestTwoReviews", latestReviews != null ? latestReviews : Collections.emptyList());

        return "product-detail-page";
    }

    @GetMapping("/{productId}/reviews")
    public String findReviewsPage(
            @PathVariable Long productId,
            @RequestParam(value = "feedbackId", required = false) Long feedbackId,
            Model model) {
        List<Review> reviews = reviewService.findReviewsByProductIdDesc(productId);
        model.addAttribute("reviews", reviews != null ? reviews : Collections.emptyList());
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
