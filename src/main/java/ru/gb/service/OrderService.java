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
        order.setStatus(OrderStatus.BEING_COLLECTED);

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
        order.setStatus(OrderStatus.BEING_COLLECTED);
        orderRepository.save(order);

        OrderNotificationDto dto = createOrderNotificationDto(order);
        log.info("Yuborilayotgan buyurtma xabari (omborxona adminiga): Order ID: {}, DTO: {}", orderId, dto);
        try {
            messagingTemplate.convertAndSend("/topic/warehouse-orders", dto);
            log.info("Buyurtma xabari muvaffaqiyatli yuborildi (omborxona adminiga): Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Xabar yuborishda xato (omborxona adminiga): Order ID: {}, Xato: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Xabar yuborishda xato: " + e.getMessage());
        }
    }

    public void sendOrderToSystemAdmin(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(OrderStatus.ON_THE_WAY);
        orderRepository.save(order);

        OrderNotificationDto dto = createOrderNotificationDto(order);
        log.info("Yuborilayotgan buyurtma xabari (tizim adminiga): Order ID: {}, DTO: {}", orderId, dto);
        try {
            messagingTemplate.convertAndSend("/topic/system-admin-orders", dto);
            log.info("Buyurtma xabari muvaffaqiyatli yuborildi (tizim adminiga): Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Xabar yuborishda xato (tizim adminiga): Order ID: {}, Xato: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Xabar yuborishda xato: " + e.getMessage());
        }
    }

    public void acceptOrderBySystemAdmin(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(OrderStatus.TAKE_IT_AWAY);
        orderRepository.save(order);
        log.info("Buyurtma tizim admini tomonidan qabul qilindi: Order ID: {}", orderId);
    }

    public void deliverOrderToCustomer(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(OrderStatus.GIVEN_TO_CUSTOMER);
        orderRepository.save(order);
        log.info("Buyurtma xaridorga topshirildi: Order ID: {}", orderId);
    }

    public List<OrderNotificationDto> getStoredOrders() {
        List<Order> orders = orderRepository.findAllByStatus(OrderStatus.TAKE_IT_AWAY);
        return orders.stream().map(this::createOrderNotificationDto).collect(Collectors.toList());
    }

    private OrderNotificationDto createOrderNotificationDto(Order order) {
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
        return dto;
    }
}