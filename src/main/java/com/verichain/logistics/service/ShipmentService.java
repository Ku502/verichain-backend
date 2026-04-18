package com.verichain.logistics.service;

import com.verichain.logistics.dto.*;
import com.verichain.logistics.exception.ResourceNotFoundException;
import com.verichain.logistics.model.Checkpoint;
import com.verichain.logistics.model.Shipment;
import com.verichain.logistics.repository.CheckpointRepository;
import com.verichain.logistics.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CheckpointRepository checkpointRepository;
    private final AIPredictionService aiPredictionService;
    private final BlockchainService blockchainService;

    // ── Create ─────────────────────────────────────────────────────────────

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        String trackingId = generateTrackingId();

        // 1. Call AI microservice for delay prediction
        AIPredictionService.AIPredictionResult prediction = aiPredictionService.predict(
                request.getOrigin(),
                request.getDestination(),
                request.getWeatherCondition(),
                request.getDistanceKm()
        );

        // 2. Build and save the shipment
        Shipment shipment = Shipment.builder()
                .trackingId(trackingId)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .senderName(request.getSenderName())
                .receiverName(request.getReceiverName())
                .weightKg(request.getWeightKg())
                .distanceKm(request.getDistanceKm())
                .currentStatus(Shipment.ShipmentStatus.PENDING)
                .expectedDelivery(request.getExpectedDelivery())
                .aiDelayRisk(prediction.getDelay_risk())
                .aiEstimatedDelayHours(prediction.getEstimated_delay_hours())
                .aiConfidenceScore(prediction.getAi_confidence_score())
                .aiRecommendedAction(prediction.getRecommended_action())
                .build();

        // 3. Log origin checkpoint to blockchain
        String txHash = blockchainService.logCheckpointToChain(
                trackingId,
                request.getOrigin(),
                prediction.getDelay_risk()
        );

        shipment.setBlockchainTxHash(txHash);
        shipment.setLoggedOnChain(txHash != null);

        Shipment saved = shipmentRepository.save(shipment);

        // 4. Create the initial checkpoint
        Checkpoint originCheckpoint = Checkpoint.builder()
                .shipment(saved)
                .location(request.getOrigin())
                .weatherCondition(request.getWeatherCondition())
                .notes("Shipment created and dispatched")
                .aiRiskAtCheckpoint(prediction.getDelay_risk())
                .blockchainTxHash(txHash)
                .build();
        checkpointRepository.save(originCheckpoint);

        log.info("Shipment created: {} | AI risk: {} | On-chain: {}",
                trackingId, prediction.getDelay_risk(), txHash != null);

        return toResponse(saved, List.of(originCheckpoint));
    }

    // ── Read ───────────────────────────────────────────────────────────────

    public List<ShipmentResponse> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(s -> toResponse(s, checkpointRepository
                        .findByShipment_TrackingIdOrderByTimestampAsc(s.getTrackingId())))
                .collect(Collectors.toList());
    }

    public ShipmentResponse getShipmentByTrackingId(String trackingId) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No shipment found with tracking ID: " + trackingId));

        List<Checkpoint> checkpoints = checkpointRepository
                .findByShipment_TrackingIdOrderByTimestampAsc(trackingId);

        return toResponse(shipment, checkpoints);
    }

    public List<ShipmentResponse> getShipmentsByStatus(Shipment.ShipmentStatus status) {
        return shipmentRepository.findByCurrentStatus(status).stream()
                .map(s -> toResponse(s, checkpointRepository
                        .findByShipment_TrackingIdOrderByTimestampAsc(s.getTrackingId())))
                .collect(Collectors.toList());
    }

    // ── Update ─────────────────────────────────────────────────────────────

    @Transactional
    public ShipmentResponse updateStatus(String trackingId, UpdateStatusRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No shipment found with tracking ID: " + trackingId));

        shipment.setCurrentStatus(request.getStatus());

        // Re-run AI prediction when a new checkpoint is added
        AIPredictionService.AIPredictionResult prediction = aiPredictionService.predict(
                shipment.getOrigin(),
                shipment.getDestination(),
                request.getWeatherCondition() != null ? request.getWeatherCondition() : "clear",
                shipment.getDistanceKm()
        );

        shipment.setAiDelayRisk(prediction.getDelay_risk());
        shipment.setAiEstimatedDelayHours(prediction.getEstimated_delay_hours());
        shipment.setAiConfidenceScore(prediction.getAi_confidence_score());
        shipment.setAiRecommendedAction(prediction.getRecommended_action());

        // Log this checkpoint to the blockchain
        String location = request.getLocation() != null ? request.getLocation() : shipment.getDestination();
        String txHash = blockchainService.logCheckpointToChain(
                trackingId, location, prediction.getDelay_risk()
        );

        Shipment updated = shipmentRepository.save(shipment);

        // Save a new checkpoint for this status update
        Checkpoint checkpoint = Checkpoint.builder()
                .shipment(updated)
                .location(location)
                .weatherCondition(request.getWeatherCondition())
                .notes(request.getNotes())
                .aiRiskAtCheckpoint(prediction.getDelay_risk())
                .blockchainTxHash(txHash)
                .build();
        checkpointRepository.save(checkpoint);

        log.info("Shipment {} status updated to {} | AI risk: {}",
                trackingId, request.getStatus(), prediction.getDelay_risk());

        List<Checkpoint> allCheckpoints = checkpointRepository
                .findByShipment_TrackingIdOrderByTimestampAsc(trackingId);

        return toResponse(updated, allCheckpoints);
    }

    // ── Delete ─────────────────────────────────────────────────────────────

    @Transactional
    public void deleteShipment(String trackingId) {
        if (!shipmentRepository.existsByTrackingId(trackingId)) {
            throw new ResourceNotFoundException("No shipment found with tracking ID: " + trackingId);
        }
        shipmentRepository.deleteById(trackingId);
        log.info("Shipment deleted: {}", trackingId);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ShipmentResponse toResponse(Shipment shipment, List<Checkpoint> checkpoints) {
        List<CheckpointResponse> checkpointResponses = checkpoints.stream()
                .map(c -> CheckpointResponse.builder()
                        .id(c.getId())
                        .location(c.getLocation())
                        .weatherCondition(c.getWeatherCondition())
                        .notes(c.getNotes())
                        .aiRiskAtCheckpoint(c.getAiRiskAtCheckpoint())
                        .blockchainTxHash(c.getBlockchainTxHash())
                        .timestamp(c.getTimestamp())
                        .build())
                .collect(Collectors.toList());

        return ShipmentResponse.builder()
                .trackingId(shipment.getTrackingId())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .senderName(shipment.getSenderName())
                .receiverName(shipment.getReceiverName())
                .weightKg(shipment.getWeightKg())
                .distanceKm(shipment.getDistanceKm())
                .currentStatus(shipment.getCurrentStatus().name())
                .aiDelayRisk(shipment.getAiDelayRisk())
                .aiEstimatedDelayHours(shipment.getAiEstimatedDelayHours())
                .aiConfidenceScore(shipment.getAiConfidenceScore())
                .aiRecommendedAction(shipment.getAiRecommendedAction())
                .blockchainTxHash(shipment.getBlockchainTxHash())
                .loggedOnChain(shipment.isLoggedOnChain())
                .expectedDelivery(shipment.getExpectedDelivery())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .checkpoints(checkpointResponses)
                .build();
    }
}
