package ru.gb.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Order;
import ru.gb.model.OrderItem;
import ru.gb.model.OrderNotificationDto;

import ru.gb.service.OrderService;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/{orderId}/send-to-warehouse")
    public ResponseEntity<Void> sendToWarehouse(@PathVariable @NotNull Long orderId) {
        log.info("Buyurtma omborxonaga yuborilmoqda: orderId={}", orderId);
        try {
            orderService.sendOrderToWarehouseAdmin(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Omborxonaga yuborishda xato: orderId={}, xato={}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orderId}/send-to-system-admin")
    public ResponseEntity<Void> sendToSystemAdmin(@PathVariable @NotNull Long orderId) {
        log.info("Buyurtma tizim adminiga yuborilmoqda: orderId={}", orderId);
        try {
            orderService.sendOrderToSystemAdmin(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Tizim adminiga yuborishda xato: orderId={}, xato={}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<Void> acceptOrder(@PathVariable @NotNull Long orderId) {
        log.info("Buyurtma tizim admini tomonidan qabul qilinmoqda: orderId={}", orderId);
        try {
            orderService.acceptOrderBySystemAdmin(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Buyurtma qabul qilishda xato: orderId={}, xato={}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable @NotNull Long orderId) {
        log.info("Buyurtma xaridorga topshirilmoqda: orderId={}", orderId);
        try {
            orderService.deliverOrderToCustomer(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Buyurtma topshirishda xato: orderId={}, xato={}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stored")
    public ResponseEntity<List<OrderNotificationDto>> getStoredOrders() {
        log.info("Saqlanayotgan buyurtmalar so'ralmoqda");
        try {
            List<OrderNotificationDto> storedOrders = orderService.getStoredOrders();
            return ResponseEntity.ok(storedOrders);
        } catch (Exception e) {
            log.error("Saqlanayotgan buyurtmalarni olishda xato: xato={}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderNotificationDto>> getUserOrders(
            @PathVariable @NotNull Long userId,
            @RequestParam(required = false) String filter) {
        log.info("Foydalanuvchi buyurtmalari so‘ralmoqda: userId={}, filter={}", userId, filter);
        try {
            List<OrderNotificationDto> dtos = orderService.getUserOrders(userId, filter);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Foydalanuvchi buyurtmalarini olishda xato: userId={}, xato={}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}