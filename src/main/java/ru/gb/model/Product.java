package ru.gb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
//* The @Data lombok was not added. The reason is that we do not need
// the toString() and hashCode() methods.
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;





}
