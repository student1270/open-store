package ru.gb.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.gb.model.Cart;
import ru.gb.model.CartProduct;
import ru.gb.model.Order;
import ru.gb.model.Product;
import ru.gb.model.User;
import ru.gb.repository.CartProductRepository;
import ru.gb.repository.CartRepository;
import ru.gb.repository.ProductRepository;
import ru.gb.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartProductService cartProductService;
    private final CartProductRepository cartProductRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @Transactional
    public Cart getOrCreateCart(HttpSession session) {
        log.info("Getting or creating cart for session: {}", session.getId());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Long userId;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userRepository.findByEmailAddress(auth.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
                if (userId == null) {
                    log.warn("User found but userId is null: Email: {}", auth.getName());
                } else {
                    log.debug("Authenticated user found: User ID: {}, Email: {}", userId, auth.getName());
                }
            } else {
                log.warn("User not found in database: Email: {}", auth.getName());
                userId = null;
            }
        } else {
            log.debug("No authenticated user found, using session cart");
            userId = null;
        }

        if (userId != null) {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUserId(userId);
                        Cart savedCart = cartRepository.save(newCart);
                        log.debug("Created new cart for user: Cart ID: {}, User ID: {}", savedCart.getId(), userId);
                        return savedCart;
                    });
            mergeSessionCartWithUserCart(session, cart);
            return cart;
        } else {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                cart.setItems(new ArrayList<>());
                session.setAttribute("cart", cart);
                log.debug("Created new session cart");
            }
            return cart;
        }
    }

    @Transactional
    public boolean addToCart(Long productId, int quantity, HttpSession session) {
        log.info("Adding product to cart: Product ID: {}, Quantity: {}, Session: {}", productId, quantity, session.getId());
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStockQuantity() < quantity) {
            log.warn("Product not found or insufficient stock: Product ID: {}, Requested Quantity: {}, Available: {}",
                    productId, quantity, product != null ? product.getStockQuantity() : 0);
            return false;
        }
        Cart cart = getOrCreateCart(session);
        cartProductService.addOrUpdateCartProduct(cart, product, quantity);
        if (cart.getUserId() != null) {
            cartRepository.save(cart);
            log.debug("Saved cart to DB: Cart ID: {}", cart.getId());
        }
        return true;
    }

    @Transactional
    public List<CartProduct> getCartItems(HttpSession session) {
        log.info("Fetching cart items for session: {}", session.getId());
        Cart cart = getOrCreateCart(session);
        if (cart.getId() == null) {
            List<CartProduct> items = cart.getItems() != null ? cart.getItems() : new ArrayList<>();
            log.debug("Returning {} session cart items", items.size());
            return items;
        }
        List<CartProduct> cartItems = cartProductRepository.findAllByCartWithProduct(cart);
        log.debug("Fetched {} DB cart items", cartItems.size());
        return cartItems;
    }

    @Transactional
    public Order confirmPurchase(HttpSession session) {
        log.info("Confirming purchase for session: {}", session.getId());
        Cart cart = getOrCreateCart(session);

        if (cart.getId() == null) {
            log.warn("Cannot confirm purchase: Cart is session-based and not linked to a user.");
            return null;
        }

        List<CartProduct> cartItems = cartProductRepository.findAllByCartWithProduct(cart);
        if (cartItems.isEmpty()) {
            log.warn("Cannot confirm purchase: Cart is empty.");
            return null;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmailAddress(auth.getName()).orElse(null);
        if (user == null) {
            log.warn("User not found for purchase confirmation: Email: {}", auth.getName());
            return null;
        }

        Order order = orderService.createOrder(
                user.getId(),
                user.getSurname(),
                user.getName(),
                user.getEmailAddress(),
                cartItems.get(0).getProduct().getCategory().getCategoryName()
        );
        log.debug("Created order: Order ID: {}", order.getId());

        for (CartProduct item : cartItems) {
            orderService.addItemToOrder(order, item.getProduct().getId(), item.getQuantity());
            log.debug("Added item to order: Product ID: {}, Quantity: {}", item.getProduct().getId(), item.getQuantity());
        }

        for (CartProduct item : cartItems) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();
            int availableQuantity = product.getStockQuantity();

            if (availableQuantity < requestedQuantity) {
                log.warn("Insufficient stock for product: Product ID: {}, Available: {}, Requested: {}",
                        product.getId(), availableQuantity, requestedQuantity);
                return null;
            }

            product.setStockQuantity(availableQuantity - requestedQuantity);
            productRepository.save(product);
            log.debug("Updated stock for product: Product ID: {}, New Stock: {}", product.getId(), product.getStockQuantity());
        }

        clearCart(session);
        orderService.sendOrderToWarehouseAdmin(order.getId());
        log.info("Purchase confirmed, order saved, and cart cleared for session: {}", session.getId());
        return order;
    }

    @Transactional
    public void removeProduct(Long productId, HttpSession session) {
        log.info("Removing product from cart: Product ID: {}, Session: {}", productId, session.getId());
        Cart cart = getOrCreateCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            cartProductService.removeProductFromCart(cart, product);
            if (cart.getUserId() != null) {
                cartRepository.save(cart);
                log.debug("Saved cart after removal: Cart ID: {}", cart.getId());
            }
        }
    }

    @Transactional
    public void clearCart(HttpSession session) {
        log.info("Clearing cart for session: {}", session.getId());
        Cart cart = getOrCreateCart(session);
        cartProductService.clearCart(cart);
        if (cart.getUserId() != null) {
            cartRepository.save(cart);
            log.debug("Saved cart after clearing: Cart ID: {}", cart.getId());
        } else {
            session.removeAttribute("cart");
            log.debug("Cleared session cart");
        }
    }

    @Transactional
    public void updateQuantity(Long productId, int quantity, HttpSession session) {
        log.info("Updating quantity: Product ID: {}, Quantity: {}, Session: {}", productId, quantity, session.getId());
        Cart cart = getOrCreateCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && quantity > 0) {
            if (quantity > product.getStockQuantity()) {
                log.warn("Requested quantity exceeds stock: Product ID: {}, Requested: {}, Available: {}",
                        productId, quantity, product.getStockQuantity());
                return;
            }
            cartProductService.addOrUpdateCartProduct(cart, product, quantity);
            if (cart.getUserId() != null) {
                cartRepository.save(cart);
                log.debug("Saved cart after quantity update: Cart ID: {}", cart.getId());
            }
        }
    }


    @Transactional
    public int getTotalQuantity(HttpSession session) {
        log.info("Calculating total quantity for session: {}", session.getId());
        Cart cart = getOrCreateCart(session);
        int totalQuantity = cartProductService.getTotalQuantity(cart);
        log.debug("Total quantity: {}", totalQuantity);
        return totalQuantity;
    }

    @Transactional
    public BigDecimal getTotalPrice(HttpSession session) {
        log.info("Calculating total price for session: {}", session.getId());
        Cart cart = getOrCreateCart(session);
        BigDecimal totalPrice = cartProductService.getTotalPrice(cart);
        log.debug("Total price: {}", totalPrice);
        return totalPrice;
    }

    @Transactional
    public void mergeSessionCartWithUserCart(HttpSession session, Cart userCart) {
        log.info("Merging session cart with user cart: Cart ID: {}, Session: {}", userCart.getId(), session.getId());
        if (userCart.getUserId() == null) {
            log.warn("User ID is null, cannot merge cart: Cart ID: {}", userCart.getId());
            return;
        }
        Cart sessionCart = (Cart) session.getAttribute("cart");
        if (sessionCart != null && sessionCart.getItems() != null && !sessionCart.getItems().isEmpty()) {
            for (CartProduct sessionItem : sessionCart.getItems()) {
                cartProductService.addOrUpdateCartProduct(userCart, sessionItem.getProduct(), sessionItem.getQuantity());
            }
            cartRepository.save(userCart);
            log.debug("Saved merged cart: Cart ID: {}", userCart.getId());
            session.removeAttribute("cart");
            log.debug("Removed session cart after merging");
        }
    }
}