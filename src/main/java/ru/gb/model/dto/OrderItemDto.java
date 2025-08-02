package ru.gb.model.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderItemDto {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;

    private String imageUrl;
}


