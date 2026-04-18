package com.verichain.logistics.repository;

import com.verichain.logistics.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {

    List<Shipment> findByCurrentStatus(Shipment.ShipmentStatus status);

    List<Shipment> findBySenderNameContainingIgnoreCase(String senderName);

    Optional<Shipment> findByTrackingId(String trackingId);

    boolean existsByTrackingId(String trackingId);
}
