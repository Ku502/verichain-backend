package com.verichain.logistics.dto;

import com.verichain.logistics.model.Shipment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private Shipment.ShipmentStatus status;

    private String location;
    private String weatherCondition;
    private String notes;
}
