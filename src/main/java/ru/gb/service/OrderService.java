package ru.gb.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Order createOrder(Long userId, String lastName, String firstName, String email, String category) {
        Order order = new Order();
        order.setUserId(userId);
        order.setLastName(lastName);
        order.setFirstName(firstName);
        order.setEmail(email);
        order.setOrderDate(LocalDateTime.now());
        order.setCategory(category);
        order.setStatus(OrderStatus.BEING_COLLECTED);

        return orderRepository.save(order);
    }


    public OrderItem addItemToOrder(Order order, Long productId, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);

        String productName = productRepository.findById(productId)
                .map(Product::getName)
                .orElse("Unknown Product");
        orderItem.setProductName(productName);

        return orderItemRepository.save(orderItem);
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
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
        }).collect(toList());

        dto.setItems(itemDtos);


        messagingTemplate.convertAndSend("/topic/warehouse-orders", dto);
    }

}
