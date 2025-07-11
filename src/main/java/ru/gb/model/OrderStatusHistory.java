package ru.gb.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Data
@NoArgsConstructor
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "status_updated_at", nullable = false)
    private LocalDateTime statusUpdatedAt;


    public OrderStatusHistory(Long orderId, String status, LocalDateTime statusUpdatedAt) {
        this.orderId = orderId;
        this.status = status;
        this.statusUpdatedAt = statusUpdatedAt;
    }
}