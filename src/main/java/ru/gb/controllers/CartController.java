package ru.gb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.service.CartService;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            RedirectAttributes redirectAttributes) {

        boolean success = cartService.addToCart(productId, quantity);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Omborda yetarli mahsulot mavjud emas");
        } else {
            redirectAttributes.addFlashAttribute("success", "Mahsulot savatga qo‘shildi");
        }

        return "redirect:/product/" + productId;
    }
}

