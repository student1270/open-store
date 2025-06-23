package ru.gb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.model.Product;
import ru.gb.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;

    public boolean addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }


        return true;
    }
}
