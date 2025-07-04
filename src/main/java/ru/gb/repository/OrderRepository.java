package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}