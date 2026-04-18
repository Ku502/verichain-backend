package com.verichain.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private Shipment shipment;

    @Column(nullable = false)
    private String location;

    private String weatherCondition;

    private String notes;

    // The AI prediction captured at this checkpoint
    private String aiRiskAtCheckpoint;

    // Blockchain tx hash for this specific checkpoint log
    private String blockchainTxHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
