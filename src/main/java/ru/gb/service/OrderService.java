package ru.gb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ru.gb.model.*;
import ru.gb.repository.OrderItemRepository;
import ru.gb.repository.OrderRepository;
import ru.gb.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Order createOrder(Long userId, String lastName, String firstName, String email, String category) {
        log.info("Yangi buyurtma yaratilmoqda: userId={}, lastName={}, firstName={}, email={}, category={}",
                userId, lastName, firstName, email, category);
        Order order = new Order();
        order.setUserId(userId);
        order.setLastName(lastName);
        order.setFirstName(firstName);
        order.setEmail(email);
        order.setCategory(category);
        order.setOrderDate(LocalDateTime.now());

        try {
            Order savedOrder = orderRepository.save(order);
            log.info("Buyurtma muvaffaqiyatli yaratildi: orderId={}", savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            log.error("Buyurtma yaratishda xato: userId={}, xato={}", userId, e.getMessage(), e);
            throw new RuntimeException("Buyurtma yaratishda xato: " + e.getMessage());
        }
    }

    public OrderItem addItemToOrder(Order order, Long productId, Integer quantity) {
        log.info("Buyurtmaga mahsulot qo'shilmoqda: orderId={}, productId={}, quantity={}",
                order.getId(), productId, quantity);
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setProductName(productRepository.findById(productId)
                .map(Product::getName)
                .orElse("Noma'lum mahsulot"));

        try {
            OrderItem savedItem = orderItemRepository.save(orderItem);
            log.info("Mahsulot muvaffaqiyatli qo'shildi: orderItemId={}", savedItem.getId());
            return savedItem;
        } catch (Exception e) {
            log.error("Mahsulot qo'shishda xato: orderId={}, productId={}, xato={}",
                    order.getId(), productId, e.getMessage(), e);
            throw new RuntimeException("Mahsulot qo'shishda xato: " + e.getMessage());
        }
    }

    public Order findById(Long orderId) {
        log.info("Buyurtma qidirilmoqda: orderId={}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Buyurtma topilmadi: orderId={}", orderId);
                    return new RuntimeException("Buyurtma topilmadi: " + orderId);
                });
    }

    public void sendOrderToWarehouseAdmin(Long orderId) {
        Order order = findById(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        OrderNotificationDto dto = new OrderNotificationDto();
        dto.setId(order.getId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setEmail(order.getEmail());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setDate(order.getOrderDate().format(formatter));

        List<OrderItemDto> itemDtos = orderItems.stream().map(item -> {
            OrderItemDto d = new OrderItemDto();
            d.setId(item.getProductId());
            d.setName(item.getProductName());
            d.setCategory(order.getCategory());
            d.setPrice(productRepository.findById(item.getProductId())
                    .map(Product::getPrice)
                    .orElse(BigDecimal.ZERO));
            d.setQuantity(item.getQuantity());
            return d;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);

        log.info("Yuborilayotgan buyurtma xabari: Order ID: {}, DTO: {}", orderId, dto);
        try {
            messagingTemplate.convertAndSend("/topic/warehouse-orders", dto);
            log.info("Buyurtma xabari muvaffaqiyatli yuborildi: Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Xabar yuborishda xato: Order ID: {}, Xato: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Xabar yuborishda xato: " + e.getMessage());
        }
    }
}