
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

//@Controller
//@RequestMapping("/order-admin")
public class OrderAdminController {

//    @Autowired
//    private ProductService productService;
//
//    @GetMapping
//    public String showAdminPage(Model model) {
//        model.addAttribute("title", "Admin Paneli");
//        return "order-admin";
//    }
//
//    @GetMapping("/add-product")
//    public String showAddProductPage(Model model) {
//        model.addAttribute("title", "Mahsulot Qo‘shish");
//        return "add-product";
//    }
//
//    @PostMapping("/add-product")
//    public String addProduct(@RequestParam("name") String name,
//                             @RequestParam("price") String price,
//                             @RequestParam("quantity") String quantity,
//                             @RequestParam("description") String description,
//                             @RequestParam("image") MultipartFile image,
//                             @RequestParam("category") String category,
//                             Model model) {
//        Product product = new Product();
//        product.setName(name);
//        product.setPrice(new BigDecimal(price));
//        product.setStockQuantity(Integer.parseInt(quantity));
//        product.setDescription(description);
//
//        if (productService.saveProduct(product, image, category)) {
//            model.addAttribute("message", "Mahsulot muvaffaqiyatli qo'shildi!");
//        } else {
//            model.addAttribute("message", "Mahsulot qo'shishda xatolik yuz berdi!");
//        }
//        return "redirect:/order-admin";
//    }
//
//    @GetMapping("/stored-orders")
//    public String showStoredOrdersPage(Model model) {
//        model.addAttribute("title", "Saqlanayotgan Buyurtmalar");
//        return "stored-orders";
//    }
}


