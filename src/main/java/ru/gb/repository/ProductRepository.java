package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}