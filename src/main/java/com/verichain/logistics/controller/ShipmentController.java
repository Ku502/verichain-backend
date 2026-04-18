package com.verichain.logistics.controller;

import com.verichain.logistics.dto.CreateShipmentRequest;
import com.verichain.logistics.dto.ShipmentResponse;
import com.verichain.logistics.dto.UpdateStatusRequest;
import com.verichain.logistics.model.Shipment;
import com.verichain.logistics.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    // ── Public: anyone can track a shipment by ID ──────────────────────────
    @GetMapping("/track/{trackingId}")
    public ResponseEntity<ShipmentResponse> trackShipment(@PathVariable String trackingId) {
        return ResponseEntity.ok(shipmentService.getShipmentByTrackingId(trackingId));
    }

    // ── Protected: requires JWT ────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(request));
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable String trackingId) {
        return ResponseEntity.ok(shipmentService.getShipmentByTrackingId(trackingId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentResponse>> getByStatus(@PathVariable Shipment.ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.getShipmentsByStatus(status));
    }

    @PatchMapping("/{trackingId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'DRIVER')")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable String trackingId,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(shipmentService.updateStatus(trackingId, request));
    }

    @DeleteMapping("/{trackingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShipment(@PathVariable String trackingId) {
        shipmentService.deleteShipment(trackingId);
        return ResponseEntity.noContent().build();
    }
}
