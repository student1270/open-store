package ru.gb.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.gb.model.Cart;
import ru.gb.model.CartProduct;
import ru.gb.model.Product;
import ru.gb.repository.CartProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private static final Logger log = LoggerFactory.getLogger(CartProductService.class);

    private final CartProductRepository cartProductRepository;

    @Transactional
    public void addOrUpdateCartProduct(Cart cart, Product product, int quantity) {
        log.info("Adding or updating cart product: Cart ID: {}, Product ID: {}, Quantity: {}",
                cart.getId(), product.getId(), quantity);
        if (quantity <= 0) {
            log.warn("Quantity is less than or equal to zero: {}", quantity);
            return;
        }

        if (cart.getId() == null) {
            List<CartProduct> items = cart.getItems();
            Optional<CartProduct> existingProduct = items.stream()
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .findFirst();
            if (existingProduct.isPresent()) {
                CartProduct cartProduct = existingProduct.get();
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                log.debug("Updated existing cart product: Product ID: {}, New Quantity: {}",
                        product.getId(), cartProduct.getQuantity());
            } else {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setCart(cart);
                cartProduct.setProduct(product);
                cartProduct.setQuantity(quantity);
                items.add(cartProduct);
                log.debug("Added new cart product: Product ID: {}, Quantity: {}",
                        product.getId(), quantity);
            }
        } else {
            Optional<CartProduct> existingProduct = cartProductRepository.findByCartAndProduct(cart, product);
            if (existingProduct.isPresent()) {
                CartProduct cartProduct = existingProduct.get();
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                cartProductRepository.save(cartProduct);
                log.debug("Updated existing cart product in DB: Product ID: {}, New Quantity: {}",
                        product.getId(), cartProduct.getQuantity());
            } else {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setCart(cart);
                cartProduct.setProduct(product);
                cartProduct.setQuantity(quantity);
                cartProductRepository.save(cartProduct);
                log.debug("Added new cart product to DB: Product ID: {}, Quantity: {}",
                        product.getId(), quantity);
            }
        }
    }

    @Transactional
    public void removeProductFromCart(Cart cart, Product product) {
        log.info("Removing product from cart: Cart ID: {}, Product ID: {}",
                cart.getId(), product.getId());
        if (cart.getId() == null) {
            cart.getItems().removeIf(item -> item.getProduct().getId().equals(product.getId()));
            log.debug("Removed product from session cart: Product ID: {}", product.getId());
        } else {
            Optional<CartProduct> existingProduct = cartProductRepository.findByCartAndProduct(cart, product);
            existingProduct.ifPresent(cartProduct -> {
                cartProductRepository.delete(cartProduct);
                log.debug("Removed product from DB cart: Product ID: {}", product.getId());
            });
        }
    }

    @Transactional
    public void clearCart(Cart cart) {
        log.info("Clearing cart: Cart ID: {}", cart.getId());
        if (cart.getId() == null) {
            cart.getItems().clear();
            log.debug("Cleared session cart");
        } else {
            cartProductRepository.deleteAllByCart(cart);
            log.debug("Cleared DB cart");
        }
    }

    @Transactional
    public int getTotalQuantity(Cart cart) {
        log.info("Calculating total quantity for cart: Cart ID: {}", cart.getId());
        if (cart.getId() == null) {
            return cart.getItems().stream()
                    .mapToInt(CartProduct::getQuantity)
                    .sum();
        }
        List<CartProduct> cartProducts = cartProductRepository.findAllByCartWithProduct(cart);
        int totalQuantity = cartProducts.stream()
                .mapToInt(CartProduct::getQuantity)
                .sum();
        log.debug("Total quantity: {}", totalQuantity);
        return totalQuantity;
    }

    @Transactional
    public BigDecimal getTotalPrice(Cart cart) {
        log.info("Calculating total price for cart: Cart ID: {}", cart.getId());
        if (cart.getId() == null) {
            return cart.getItems().stream()
                    .map(item -> {
                        BigDecimal price = item.getProduct().getPrice();
                        log.debug("Processing session cart product: Product ID: {}, Price: {}, Quantity: {}",
                                item.getProduct().getId(), price, item.getQuantity());
                        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        List<CartProduct> cartProducts = cartProductRepository.findAllByCartWithProduct(cart);
        BigDecimal totalPrice = cartProducts.stream()
                .map(cartProduct -> {
                    BigDecimal price = cartProduct.getProduct().getPrice();
                    log.debug("Processing DB cart product: Product ID: {}, Price: {}, Quantity: {}",
                            cartProduct.getProduct().getId(), price, cartProduct.getQuantity());
                    return price.multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("Total price: {}", totalPrice);
        return totalPrice;
    }
}