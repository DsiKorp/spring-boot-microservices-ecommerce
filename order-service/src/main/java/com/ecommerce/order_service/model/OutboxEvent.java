package com.ecommerce.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId; // Aquí guardarás el orderNumber

    @Column(name = "event_type", nullable = false, length = 100)
    private String type; // Ej: "OrderPlacedEvent"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload; // El JSON completo del evento

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status; // PENDING o PROCESSED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private boolean processed;  // Para saber si el evento ya fue procesado

//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now();
//        if (this.status == null) {
//            this.status = OutboxStatus.PENDING;
//        }
//    }
}