package com.verichain.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @Column(name = "tracking_id", unique = true, nullable = false)
    private String trackingId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private double weightKg;

    @Column(nullable = false)
    private int distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus currentStatus;

    // AI Prediction fields — populated by the Python microservice
    private String aiDelayRisk;         // LOW / MEDIUM / HIGH
    private int aiEstimatedDelayHours;
    private double aiConfidenceScore;
    private String aiRecommendedAction; // PROCEED / REROUTE

    // Blockchain fields — populated after on-chain logging
    private String blockchainTxHash;
    private boolean loggedOnChain;

    private LocalDateTime expectedDelivery;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Checkpoint> checkpoints = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.loggedOnChain = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ShipmentStatus {
        PENDING, IN_TRANSIT, DELAYED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }
}
