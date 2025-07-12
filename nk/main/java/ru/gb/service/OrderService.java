package ru.gb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.gb.model.*;
import ru.gb.repository.OrderItemRepository;
import ru.gb.repository.OrderRepository;
import ru.gb.repository.OrderStatusHistoryRepository;
import ru.gb.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final OrderStatusHistoryRepository statusHistoryRepository;

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
        order.setStatusUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.BEING_COLLECTED);

        try {
            Order savedOrder = orderRepository.save(order);
            OrderStatusHistory history = new OrderStatusHistory(savedOrder.getId(), OrderStatus.BEING_COLLECTED.getValue(), LocalDateTime.now());
            statusHistoryRepository.save(history);
            log.info("Buyurtma muvaffaqiyatli yaratildi: orderId={}", savedOrder.getId());
            notifyUser(savedOrder.getId());
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

    public List<OrderNotificationDto> getUserOrders(Long userId, String filter) {
        log.info("Foydalanuvchi buyurtmalarini olish: userId={}, filter={}", userId, filter);
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders == null) {
            orders = new ArrayList<>();
        }

        List<OrderNotificationDto> dtos = orders.stream()
                .filter(order -> order != null && order.getStatus() != null)
                .map(this::createOrderNotificationDto)
                .collect(Collectors.toList());

        if ("current".equalsIgnoreCase(filter)) {
            dtos = dtos.stream()
                    .filter(dto -> Arrays.asList("BEING_COLLECTED", "ON_THE_WAY", "TAKE_IT_AWAY")
                            .contains(dto.getStatus()))
                    .collect(Collectors.toList());
        } else if ("unfinished".equalsIgnoreCase(filter)) {
            dtos = dtos.stream()
                    .filter(dto -> "PENDING_PAYMENT".equals(dto.getStatus()))
                    .collect(Collectors.toList());
        }

        log.debug("Qaytarilgan buyurtmalar soni: {}", dtos.size());
        return dtos;
    }

    public void sendOrderToWarehouseAdmin(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(OrderStatus.ON_THE_WAY);
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory(orderId, OrderStatus.ON_THE_WAY.getValue(), LocalDateTime.now());
        statusHistoryRepository.save(history);

        notifyUser(orderId);
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
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory(orderId, OrderStatus.ON_THE_WAY.getValue(), LocalDateTime.now());
        statusHistoryRepository.save(history);

        notifyUser(orderId);
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
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        OrderStatusHistory history = new OrderStatusHistory(orderId, OrderStatus.TAKE_IT_AWAY.getValue(), LocalDateTime.now());
        statusHistoryRepository.save(history);
        notifyUser(orderId);
        log.info("Buyurtma tizim admini tomonidan qabul qilindi: Order ID: {}", orderId);
    }

    public void deliverOrderToCustomer(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(OrderStatus.GIVEN_TO_CUSTOMER);
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        OrderStatusHistory history = new OrderStatusHistory(orderId, OrderStatus.GIVEN_TO_CUSTOMER.getValue(), LocalDateTime.now());
        statusHistoryRepository.save(history);
        notifyUser(orderId);
        log.info("Buyurtma xaridorga topshirildi: Order ID: {}", orderId);
    }

    public List<OrderNotificationDto> getStoredOrders() {
        List<Order> orders = orderRepository.findAllByStatus(OrderStatus.TAKE_IT_AWAY);
        return orders.stream().map(this::createOrderNotificationDto).collect(Collectors.toList());
    }

    private void notifyUser(Long orderId) {
        Order order = findById(orderId);
        OrderNotificationDto dto = createOrderNotificationDto(order);
        log.info("Foydalanuvchiga buyurtma xabari yuborilmoqda: orderId={}", orderId);
        try {
            messagingTemplate.convertAndSend("/topic/user-orders/" + order.getUserId(), dto);
            log.info("Foydalanuvchiga xabar muvaffaqiyatli yuborildi: orderId={}", orderId);
        } catch (Exception e) {
            log.error("Foydalanuvchiga xabar yuborishda xato: orderId={}, xato={}", orderId, e.getMessage(), e);
        }
    }

    private OrderNotificationDto createOrderNotificationDto(Order order) {
        if (order == null) {
            return new OrderNotificationDto(); // Yoki exception tashlang
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        OrderNotificationDto dto = new OrderNotificationDto();
        dto.setId(order.getId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setEmail(order.getEmail());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setDate(order.getOrderDate() != null ? order.getOrderDate().format(formatter) : LocalDateTime.now().format(formatter));

        List<OrderItemDto> itemDtos = orderItems.stream()
                .filter(item -> item != null)
                .map(item -> {
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

        List<StatusHistoryDto> historyDtos = statusHistoryRepository.findByOrderId(order.getId()).stream()
                .filter(history -> history != null)
                .map(history -> {
                    StatusHistoryDto h = new StatusHistoryDto();
                    h.setStatus(history.getStatus());
                    h.setStatusUpdatedAt(history.getStatusUpdatedAt() != null ? history.getStatusUpdatedAt().format(formatter) : "");
                    return h;
                }).collect(Collectors.toList());
        dto.setStatusHistory(historyDtos);

        dto.setStatus(order.getStatus() != null ? order.getStatus().getValue() : "");
        dto.setStatusUpdatedAt(order.getStatusUpdatedAt() != null ? order.getStatusUpdatedAt().format(formatter) : "");

        dto.setItems(itemDtos);
        return dto;
    }
}