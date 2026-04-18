package com.verichain.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {

    private String trackingId;
    private String origin;
    private String destination;
    private String senderName;
    private String receiverName;
    private double weightKg;
    private int distanceKm;
    private String currentStatus;

    // AI Prediction
    private String aiDelayRisk;
    private int aiEstimatedDelayHours;
    private double aiConfidenceScore;
    private String aiRecommendedAction;

    // Blockchain
    private String blockchainTxHash;
    private boolean loggedOnChain;

    private LocalDateTime expectedDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CheckpointResponse> checkpoints;
}
