package ru.gb.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.model.CartProduct;
import ru.gb.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (quantity < 1) {
            redirectAttributes.addFlashAttribute("error", "Miqdor 1 dan kam bo‘lmasligi kerak");
            return "redirect:/product/" + productId;
        }
        boolean success = cartService.addToCart(productId, quantity, session);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Omborda yetarli mahsulot mavjud emas");
        } else {
            redirectAttributes.addFlashAttribute("success", "Mahsulot savatga qo‘shildi");
        }
        return "redirect:/product/" + productId;
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        List<CartProduct> cartItems = cartService.getCartItems(session);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalQuantity", cartService.getTotalQuantity(session));
        model.addAttribute("totalPrice", cartService.getTotalPrice(session));
        return "cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (quantity < 1) {
            redirectAttributes.addFlashAttribute("error", "Miqdor 1 dan kam bo‘lmasligi kerak");
            return "redirect:/cart";
        }
        cartService.updateQuantity(productId, quantity, session);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId, HttpSession session) {
        cartService.removeProduct(productId, session);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            session.setAttribute("redirectAfterLogin", "/cart/checkout");
            redirectAttributes.addFlashAttribute("error", "Xaridni amalga oshirish uchun ro‘yxatdan o‘ting!");
            return "redirect:/login";
        }
        return "redirect:/cart/confirm";
    }

    @GetMapping("/confirm")
    public String showConfirmPage(Model model) {
        model.addAttribute("message", "Ishonchingiz komilmi?");
        return "confirm-checkout";
    }

    @PostMapping("/confirm")
    public String confirmPurchase(HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("success", "Xarid muvaffaqiyatli amalga oshirildi!");
            return "redirect:/home";
        }
        return "redirect:/cart";
    }

    @PostMapping("/cancel")
    public String cancelPurchase(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Xarid bekor qilindi!");
        return "redirect:/cart";
    }
}