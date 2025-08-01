package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.model.Product;
import ru.gb.service.ProductService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order-admin")
public class OrderAdminRestController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> showAdminPage() {
        return ResponseEntity.ok(Map.of("title", "Admin Paneli"));
    }

    @GetMapping("/add-product")
    public ResponseEntity<?> showAddProductPage() {
        return ResponseEntity.ok(Map.of("title", "Mahsulot Qo‘shish"));
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@RequestParam("name") String name,
                                        @RequestParam("price") String price,
                                        @RequestParam("quantity") String quantity,
                                        @RequestParam("description") String description,
                                        @RequestParam("image") MultipartFile image,
                                        @RequestParam("category") String category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(Integer.parseInt(quantity));
        product.setDescription(description);

        boolean success = productService.saveProduct(product, image, category);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("message", "Mahsulot muvaffaqiyatli qo'shildi!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Mahsulot qo'shishda xatolik yuz berdi!");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/stored-orders")
    public ResponseEntity<?> showStoredOrdersPage() {
        return ResponseEntity.ok(Map.of("title", "Saqlanayotgan Buyurtmalar"));
    }
}