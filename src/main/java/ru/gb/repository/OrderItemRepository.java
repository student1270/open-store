package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}