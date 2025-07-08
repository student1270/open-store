package ru.gb.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Order;
import ru.gb.model.OrderItem;
import ru.gb.service.OrderService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String lastName,
            @RequestParam @NotNull String firstName,
            @RequestParam @NotNull String email,
            @RequestParam @NotNull String category) {
        log.info("Buyurtma yaratish so'rovi: userId={}, lastName={}, firstName={}, email={}, category={}",
                userId, lastName, firstName, email, category);
        try {
            Order order = orderService.createOrder(userId, lastName, firstName, email, category);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Buyurtma yaratishda xato: userId={}, xato={}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addItemToOrder(
            @PathVariable @NotNull Long orderId,
            @RequestParam @NotNull Long productId,
            @RequestParam @NotNull Integer quantity) {
        log.info("Buyurtmaga mahsulot qo'shish so'rovi: orderId={}, productId={}, quantity={}",
                orderId, productId, quantity);
        try {
            Order order = orderService.findById(orderId);
            OrderItem orderItem = orderService.addItemToOrder(order, productId, quantity);
            return ResponseEntity.ok(orderItem);
        } catch (Exception e) {
            log.error("Mahsulot qo'shishda xato: orderId={}, productId={}, xato={}",
                    orderId, productId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}