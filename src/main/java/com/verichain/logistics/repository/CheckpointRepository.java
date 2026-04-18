package com.verichain.logistics.repository;

import com.verichain.logistics.model.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    List<Checkpoint> findByShipment_TrackingIdOrderByTimestampAsc(String trackingId);
}
