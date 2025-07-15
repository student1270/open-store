//package ru.gb.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import ru.gb.model.Product;
//import ru.gb.service.ProductService;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/order-admin")
//public class OrderAdminController {
//
//    @Autowired
//    private ProductService productService;
//
//    @GetMapping
//    public ResponseEntity<?> showAdminPage() {
//        return ResponseEntity.ok(Map.of("title", "Admin Paneli"));
//    }
//
//    @GetMapping("/add-product")
//    public ResponseEntity<?> showAddProductPage() {
//        return ResponseEntity.ok(Map.of("title", "Mahsulot Qo‘shish"));
//    }
//
//    @PostMapping("/add-product")
//    public ResponseEntity<?> addProduct(@RequestParam("name") String name,
//                                        @RequestParam("price") String price,
//                                        @RequestParam("quantity") String quantity,
//                                        @RequestParam("description") String description,
//                                        @RequestParam("image") MultipartFile image,
//                                        @RequestParam("category") String category) {
//        Product product = new Product();
//        product.setName(name);
//        product.setPrice(new BigDecimal(price));
//        product.setStockQuantity(Integer.parseInt(quantity));
//        product.setDescription(description);
//
//        boolean success = productService.saveProduct(product, image, category);
//        Map<String, Object> response = new HashMap<>();
//        if (success) {
//            response.put("message", "Mahsulot muvaffaqiyatli qo'shildi!");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("error", "Mahsulot qo'shishda xatolik yuz berdi!");
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//
//    @GetMapping("/stored-orders")
//    public ResponseEntity<?> showStoredOrdersPage() {
//        return ResponseEntity.ok(Map.of("title", "Saqlanayotgan Buyurtmalar"));
//    }
//}


package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.model.Product;
import ru.gb.service.ProductService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/order-admin")
public class OrderAdminController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showAdminPage(Model model) {
        model.addAttribute("title", "Admin Paneli");
        return "order-admin";
    }

    @GetMapping("/add-product")
    public String showAddProductPage(Model model) {
        model.addAttribute("title", "Mahsulot Qo‘shish");
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("price") String price,
                             @RequestParam("quantity") String quantity,
                             @RequestParam("description") String description,
                             @RequestParam("image") MultipartFile image,
                             @RequestParam("category") String category,
                             Model model) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(Integer.parseInt(quantity));
        product.setDescription(description);

        if (productService.saveProduct(product, image, category)) {
            model.addAttribute("message", "Mahsulot muvaffaqiyatli qo'shildi!");
        } else {
            model.addAttribute("message", "Mahsulot qo'shishda xatolik yuz berdi!");
        }
        return "redirect:/order-admin";
    }

    @GetMapping("/stored-orders")
    public String showStoredOrdersPage(Model model) {
        model.addAttribute("title", "Saqlanayotgan Buyurtmalar");
        return "stored-orders";
    }
}


