package ru.gb.service;

import lombok.RequiredArgsConstructor;
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

    private final CartProductRepository cartProductRepository;

    public void addOrUpdateCartProduct(Cart cart, Product product, int quantity) {
        if (quantity <= 0) {
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
            } else {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setCart(cart);
                cartProduct.setProduct(product);
                cartProduct.setQuantity(quantity);
                items.add(cartProduct);
            }
        } else {
            Optional<CartProduct> existingProduct = cartProductRepository.findByCartAndProduct(cart, product);
            if (existingProduct.isPresent()) {
                CartProduct cartProduct = existingProduct.get();
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                cartProductRepository.save(cartProduct);
            } else {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setCart(cart);
                cartProduct.setProduct(product);
                cartProduct.setQuantity(quantity);
                cartProductRepository.save(cartProduct);
            }
        }
    }

    public void removeProductFromCart(Cart cart, Product product) {
        if (cart.getId() == null) {
            cart.getItems().removeIf(item -> item.getProduct().getId().equals(product.getId()));
        } else {
            Optional<CartProduct> existingProduct = cartProductRepository.findByCartAndProduct(cart, product);
            existingProduct.ifPresent(cartProductRepository::delete);
        }
    }

    public void clearCart(Cart cart) {
        if (cart.getId() == null) {
            cart.getItems().clear();
        } else {
            cartProductRepository.deleteAllByCart(cart);
        }
    }

    public int getTotalQuantity(Cart cart) {
        if (cart.getId() == null) {
            return cart.getItems().stream().mapToInt(CartProduct::getQuantity).sum();
        }
        return cartProductRepository.findAllByCart(cart).stream()
                .mapToInt(CartProduct::getQuantity)
                .sum();
    }

    public BigDecimal getTotalPrice(Cart cart) {
        if (cart.getId() == null) {
            return cart.getItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return cartProductRepository.findAllByCart(cart).stream()
                .map(cartProduct -> cartProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}