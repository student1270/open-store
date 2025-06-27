package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.model.Cart;
import ru.gb.model.CartProduct;
import ru.gb.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByCartAndProduct(Cart cart, Product product);

    List<CartProduct> findAllByCart(Cart cart);

    void deleteAllByCart(Cart cart);
}