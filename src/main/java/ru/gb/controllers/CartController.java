package ru.gb.controllers;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.model.Cart;
import ru.gb.model.CartProduct;
import ru.gb.model.Order;
import ru.gb.model.Roles;
import ru.gb.repository.CartRepository;
import ru.gb.service.CartProductService;
import ru.gb.service.CartService;
import ru.gb.service.impl.UserDetailsImpl;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);



    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartProductService cartProductService;

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
            return "redirect:/product/" + productId;
        }
        redirectAttributes.addFlashAttribute("success", "Mahsulot savatga qo‘shildi");
        return "redirect:/cart";
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();
            log.info("Savat ko‘rinishi: User ID: {}, Email: {}, Roles: {}", userId, userDetails.getUsername(), authentication.getAuthorities());
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
                Cart dbCart = cartRepository.findByUserId(userId).orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
                for (CartProduct item : sessionCart.getItems()) {
                    cartProductService.addOrUpdateCartProduct(dbCart, item.getProduct(), item.getQuantity());
                }
                session.removeAttribute("cart");
            }
        } else {
            log.info("Savat ko‘rinishi: Foydalanuvchi autentifikatsiya qilinmagan");
        }

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
        log.info("Checkout so‘rovi. Sessiya ID: {}, Autentifikatsiya: {}, IsAuthenticated: {}, Rollar: {}, Principal: {}",
                session.getId(),
                auth != null ? auth.getName() : "null",
                auth != null && auth.isAuthenticated(),
                auth != null ? auth.getAuthorities() : "null",
                auth != null ? auth.getPrincipal() : "null");

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            log.warn("Foydalanuvchi autentifikatsiya qilinmagan, /login sahifasiga yo‘naltirilmoqda. Sessiya ID: {}", session.getId());
            session.setAttribute("redirectAfterLogin", "/cart");
            redirectAttributes.addFlashAttribute("error", "Xaridni amalga oshirish uchun tizimga kiring!");
            return "redirect:/login";
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Roles role = userDetails.getUser().getRole();
        log.info("Foydalanuvchi autentifikatsiya qilindi. User ID: {}, Rol: {}, Email: {}",
                userDetails.getUser().getId(), role, userDetails.getUsername());

        if (!Roles.USER.equals(role)) {
            log.warn("Foydalanuvchi roli USER emas: {}", role);
            redirectAttributes.addFlashAttribute("error", "Faqat oddiy foydalanuvchilar xarid qilishi mumkin.");
            return "redirect:/cart";
        }

        return "confirm-checkout";
    }


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/confirm")
    public String confirmPurchase(HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            redirectAttributes.addFlashAttribute("error", "Xaridni tasdiqlash uchun tizimga kiring!");
            return "redirect:/login";
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        if (!Roles.USER.equals(userDetails.getUser().getRole())) {
            redirectAttributes.addFlashAttribute("error", "Faqat oddiy foydalanuvchilar xarid qilishi mumkin.");
            return "redirect:/cart";
        }

        Order order = cartService.confirmPurchase(session);
        if (order != null) {

            messagingTemplate.convertAndSend("/topic/warehouse-orders", order);

            redirectAttributes.addFlashAttribute("success", "Xarid muvaffaqiyatli amalga oshirildi!");
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Xaridni amalga oshirishda xatolik!");
            return "redirect:/cart";
        }
    }

    @PostMapping("/cancel")
    public String cancelPurchase(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Xarid bekor qilindi!");
        return "redirect:/cart";
    }
}