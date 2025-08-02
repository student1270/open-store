package ru.gb.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderNotificationDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String date;
    private List<OrderItemDto> items;
    private List<StatusHistoryDto> statusHistory;
    private String status;
    private String statusUpdatedAt;
}