package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Order;
import ru.gb.model.OrderItem;
import ru.gb.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order createOrder(
            @RequestParam Long userId,
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam String email,
            @RequestParam String category) {
        return orderService.createOrder(userId, lastName, firstName, email, category);
    }

    @PostMapping("/{orderId}/items")
    public OrderItem addItemToOrder(
            @PathVariable Long orderId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Order order = orderService.findById(orderId);
        return orderService.addItemToOrder(order, productId, quantity);
    }
}