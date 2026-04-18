package com.verichain.logistics.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AIPredictionService {

    @Value("${app.ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AIPredictionResult predict(String origin, String destination,
                                      String weatherCondition, int distanceKm) {
        try {
            String url = aiServiceUrl + "/api/predict-delay";

            AIPredictionRequest requestBody = new AIPredictionRequest(
                    origin, destination, weatherCondition, distanceKm
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AIPredictionRequest> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<AIPredictionResult> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AIPredictionResult.class
            );

            AIPredictionResult result = response.getBody();
            log.info("AI prediction received: risk={}, delay={}h, confidence={}",
                    result.getDelay_risk(),
                    result.getEstimated_delay_hours(),
                    result.getAi_confidence_score());

            return result;

        } catch (Exception e) {
            log.warn("AI microservice unavailable ({}). Falling back to default LOW risk.", e.getMessage());
            // Graceful fallback — system works even if AI is down
            AIPredictionResult fallback = new AIPredictionResult();
            fallback.setPrediction_status("FALLBACK");
            fallback.setDelay_risk("LOW");
            fallback.setEstimated_delay_hours(0);
            fallback.setAi_confidence_score(0.0);
            fallback.setRecommended_action("PROCEED");
            return fallback;
        }
    }

    // ── Inner classes for serialization ──────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIPredictionRequest {
        private String origin;
        private String destination;
        private String weather_condition;
        private int distance_km;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIPredictionResult {
        private String prediction_status;
        private String delay_risk;
        private int estimated_delay_hours;
        private double ai_confidence_score;
        private String recommended_action;
    }
}
