package ru.gb.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.gb.model.Cart;
import ru.gb.model.CartProduct;
import ru.gb.model.Product;
import ru.gb.model.User;
import ru.gb.repository.CartRepository;
import ru.gb.repository.ProductRepository;
import ru.gb.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartProductService cartProductService;
    private final UserRepository userRepository;

    public Cart getOrCreateCart(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userRepository.findByEmailAddress(auth.getName()).orElse(null); // Username o‘rniga email
            if (user != null) {
                userId = user.getId();
            } else {
                userId = null;
            }
        } else {
            userId = null;
        }
        if (userId != null) {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUserId(userId);
                        return cartRepository.save(newCart);
                    });
            mergeSessionCartWithUserCart(session, cart);
            return cart;
        } else {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                cart.setItems(new ArrayList<>());
                session.setAttribute("cart", cart);
            }
            return cart;
        }
    }
    public boolean addToCart(Long productId, int quantity, HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }
        Cart cart = getOrCreateCart(session);
        cartProductService.addOrUpdateCartProduct(cart, product, quantity);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()) && cart.getUserId() != null) {
            cartRepository.save(cart);
        }
        return true;
    }

    public List<CartProduct> getCartItems(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        return cart.getItems() != null ? cart.getItems() : new ArrayList<>();
    }

    public void removeProduct(Long productId, HttpSession session) {
        Cart cart = getOrCreateCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            cartProductService.removeProductFromCart(cart, product);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()) && cart.getUserId() != null) {
                cartRepository.save(cart);
            }
        }
    }

    public void clearCart(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        cartProductService.clearCart(cart);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()) && cart.getUserId() != null) {
            cartRepository.save(cart);
        } else {
            session.removeAttribute("cart");
        }
    }

    public void updateQuantity(Long productId, int quantity, HttpSession session) {
        Cart cart = getOrCreateCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && quantity > 0) {
            cartProductService.addOrUpdateCartProduct(cart, product, quantity);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()) && cart.getUserId() != null) {
                cartRepository.save(cart);
            }
        }
    }

    public int getTotalQuantity(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        return cartProductService.getTotalQuantity(cart);
    }

    public BigDecimal getTotalPrice(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        return cartProductService.getTotalPrice(cart);
    }

    private void mergeSessionCartWithUserCart(HttpSession session, Cart userCart) {
        Cart sessionCart = (Cart) session.getAttribute("cart");
        if (sessionCart != null && sessionCart.getItems() != null && !sessionCart.getItems().isEmpty()) {
            for (CartProduct sessionItem : sessionCart.getItems()) {
                cartProductService.addOrUpdateCartProduct(userCart, sessionItem.getProduct(), sessionItem.getQuantity());
            }
            if (userCart.getUserId() != null) {
                cartRepository.save(userCart);
            }
            session.removeAttribute("cart");
        }
    }
}