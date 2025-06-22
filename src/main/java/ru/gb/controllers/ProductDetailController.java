package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.service.ProductService;

@Controller()
@RequestMapping("/product")
public class ProductDetailController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId , Model model){
        model.addAttribute("productDetailPage" , productService.findProductsById(productId));
        return "product-detail-page";
    }

}
