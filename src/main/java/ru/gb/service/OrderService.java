package ru.gb.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.model.Order;
import ru.gb.model.OrderItem;
import ru.gb.model.Product;
import ru.gb.repository.OrderItemRepository;
import ru.gb.repository.OrderRepository;
import ru.gb.repository.ProductRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(Long userId, String lastName, String firstName, String email, String category) {
        Order order = new Order();
        order.setUserId(userId);
        order.setLastName(lastName);
        order.setFirstName(firstName);
        order.setEmail(email);
        order.setOrderDate(LocalDateTime.now());
        order.setCategory(category);

        return orderRepository.save(order);
    }

    public OrderItem addItemToOrder(Order order, Long productId, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);

        // Mahsulot nomini olish va saqlash
        String productName = productRepository.findById(productId)
                .map(Product::getName) // `getName()` mahsulot nomini qaytarishi kerak
                .orElse("Unknown Product");
        orderItem.setProductName(productName);

        return orderItemRepository.save(orderItem);
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}