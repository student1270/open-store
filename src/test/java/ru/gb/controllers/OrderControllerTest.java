package ru.gb.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.gb.model.*;
import ru.gb.model.dto.OrderItemDto;
import ru.gb.model.dto.OrderNotificationDto;
import ru.gb.service.OrderService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private OrderItem testOrderItem;
    private OrderNotificationDto testNotificationDto;
    private OrderItemDto testOrderItemDto;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setStatus(OrderStatus.BEING_COLLECTED);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setQuantity(2);

        testNotificationDto = new OrderNotificationDto();
        testNotificationDto.setId(1L);
        testNotificationDto.setStatus("BEING_COLLECTED");

        testOrderItemDto = new OrderItemDto();
        testOrderItemDto.setId(1L);
        testOrderItemDto.setQuantity(2);
    }

    @Test
    void createOrder_Success() {
        when(orderService.createOrder(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(1L, "Doe", "John", "john@example.com", "Electronics");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(orderService).createOrder(1L, "Doe", "John", "john@example.com", "Electronics");
    }

    @Test
    void createOrder_Failure() {
        when(orderService.createOrder(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Error creating order"));

        ResponseEntity<Order> response = orderController.createOrder(1L, "Doe", "John", "john@example.com", "Electronics");

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void addItemToOrder_Success() {
        when(orderService.findById(anyLong())).thenReturn(testOrder);
        when(orderService.addItemToOrder(any(Order.class), anyLong(), anyInt()))
                .thenReturn(testOrderItem);

        ResponseEntity<OrderItem> response = orderController.addItemToOrder(1L, 1L, 2);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(orderService).addItemToOrder(testOrder, 1L, 2);
    }

    @Test
    void addItemToOrder_Failure() {
        when(orderService.findById(anyLong())).thenReturn(testOrder);
        when(orderService.addItemToOrder(any(Order.class), anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Error adding item"));

        ResponseEntity<OrderItem> response = orderController.addItemToOrder(1L, 1L, 2);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void sendToWarehouse_Success() {
        doNothing().when(orderService).sendOrderToWarehouseAdmin(anyLong());

        ResponseEntity<Void> response = orderController.sendToWarehouse(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).sendOrderToWarehouseAdmin(1L);
    }

    @Test
    void sendToWarehouse_Failure() {
        doThrow(new RuntimeException("Error sending to warehouse"))
                .when(orderService).sendOrderToWarehouseAdmin(anyLong());

        ResponseEntity<Void> response = orderController.sendToWarehouse(1L);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void sendToSystemAdmin_Success() {
        doNothing().when(orderService).sendOrderToSystemAdmin(anyLong());

        ResponseEntity<Void> response = orderController.sendToSystemAdmin(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).sendOrderToSystemAdmin(1L);
    }

    @Test
    void sendToSystemAdmin_Failure() {
        doThrow(new RuntimeException("Error sending to admin"))
                .when(orderService).sendOrderToSystemAdmin(anyLong());

        ResponseEntity<Void> response = orderController.sendToSystemAdmin(1L);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void acceptOrder_Success() {
        doNothing().when(orderService).acceptOrderBySystemAdmin(anyLong());

        ResponseEntity<Void> response = orderController.acceptOrder(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).acceptOrderBySystemAdmin(1L);
    }

    @Test
    void acceptOrder_Failure() {
        doThrow(new RuntimeException("Error accepting order"))
                .when(orderService).acceptOrderBySystemAdmin(anyLong());

        ResponseEntity<Void> response = orderController.acceptOrder(1L);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void deliverOrder_Success() {
        doNothing().when(orderService).deliverOrderToCustomer(anyLong());

        ResponseEntity<Void> response = orderController.deliverOrder(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).deliverOrderToCustomer(1L);
    }

    @Test
    void deliverOrder_Failure() {
        doThrow(new RuntimeException("Error delivering order"))
                .when(orderService).deliverOrderToCustomer(anyLong());

        ResponseEntity<Void> response = orderController.deliverOrder(1L);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getStoredOrders_Success() {
        when(orderService.getStoredOrders())
                .thenReturn(Arrays.asList(testNotificationDto));

        ResponseEntity<List<OrderNotificationDto>> response = orderController.getStoredOrders();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void getStoredOrders_Failure() {
        when(orderService.getStoredOrders())
                .thenThrow(new RuntimeException("Error getting stored orders"));

        ResponseEntity<List<OrderNotificationDto>> response = orderController.getStoredOrders();

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getUserOrders_Success() {
        when(orderService.getUserOrders(anyLong(), anyString()))
                .thenReturn(Arrays.asList(testNotificationDto));

        ResponseEntity<List<OrderNotificationDto>> response = orderController.getUserOrders(1L, "current");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void getUserOrders_Failure() {
        when(orderService.getUserOrders(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Error getting user orders"));

        ResponseEntity<List<OrderNotificationDto>> response = orderController.getUserOrders(1L, "current");

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getOrderItems_Success() {
        when(orderService.getOrderItems(anyLong()))
                .thenReturn(Arrays.asList(testOrderItemDto));

        ResponseEntity<List<OrderItemDto>> response = orderController.getOrderItems(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void getOrderItems_Failure() {
        when(orderService.getOrderItems(anyLong()))
                .thenThrow(new RuntimeException("Error getting order items"));

        ResponseEntity<List<OrderItemDto>> response = orderController.getOrderItems(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}