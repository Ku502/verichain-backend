package com.verichain.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Sender name is required")
    private String senderName;

    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @Positive(message = "Weight must be a positive number")
    private double weightKg;

    @Positive(message = "Distance must be a positive number")
    private int distanceKm;

    // Fed to the AI microservice for prediction
    private String weatherCondition = "clear";

    private LocalDateTime expectedDelivery;
}
