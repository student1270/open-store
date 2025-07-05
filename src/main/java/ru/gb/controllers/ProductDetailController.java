package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gb.service.ProductService;
import ru.gb.service.ReviewService;

@Controller
@RequestMapping("/product")
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;


    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        model.addAttribute("productDetailPage", productService.findProductsById(productId));
        model.addAttribute("latestTwoReviews", reviewService.findTop2ByProductIdOrderByLocalDateTimeDesc(productId));
        return "product-detail-page";
    }


    @GetMapping("/{productId}/reviews")
    public String findReviewsPage(
            @PathVariable Long productId,
            @RequestParam(value = "feedbackId", required = false) Long feedbackId,
            Model model
    ) {
        model.addAttribute("reviews", reviewService.findReviewsByProductIdDesc(productId));
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
