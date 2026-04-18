package com.verichain.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckpointResponse {

    private Long id;
    private String location;
    private String weatherCondition;
    private String notes;
    private String aiRiskAtCheckpoint;
    private String blockchainTxHash;
    private LocalDateTime timestamp;
}
