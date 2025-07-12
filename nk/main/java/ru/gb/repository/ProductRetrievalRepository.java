package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gb.controllers.ProductRetrievalController;
import ru.gb.model.Product;

import java.util.List;

public interface ProductRetrievalRepository extends JpaRepository<Product , Long> {
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.createdAt DESC")
    List<Product> findProductsByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.price ASC ")
    List<Product> findProductsByCategoryAndPriceAsc(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.price DESC")
    List<Product> findProductsByCategoryAndPriceDesc(@Param("categoryId") Long categoryId);

}
