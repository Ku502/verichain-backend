package com.verichain.logistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hidden endpoint — not documented in the main README.
 *
 * Hint left in application.properties comments for curious recruiters:
 *   curl -X GET https://your-deployment.up.railway.app/api/v1/developer/status
 */
@RestController
@RequestMapping("/api/v1/developer")
public class HireMeController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDeveloperStatus() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", "AVAILABLE_FOR_HIRE");
        response.put("message",
                "Congratulations on finding the hidden endpoint. " +
                "If you are reading this, you clearly appreciate developers who think beyond the obvious.");

        response.put("architecture_demonstrated", List.of(
                "Spring Boot microservices orchestration",
                "JWT stateless authentication (HMAC-SHA256)",
                "BCrypt password hashing",
                "Cross-service REST communication (Java → Python)",
                "Web3j Ethereum blockchain integration",
                "Role-based access control (ADMIN / MANAGER / DRIVER / VIEWER)",
                "Global exception handling with structured error responses",
                "Clean MVC layering: Controller → Service → Repository"
        ));

        response.put("stack", Map.of(
                "backend", "Java 17 + Spring Boot 3.2",
                "ai_microservice", "Python 3 + FastAPI",
                "blockchain", "Solidity + Web3j + Ethereum Sepolia",
                "database", "H2 (dev) / PostgreSQL (prod)",
                "frontend", "Vanilla JS / HTML / CSS deployed on Netlify"
        ));

        response.put("open_to_roles", List.of(
                "Java Backend Developer",
                "Full Stack Developer",
                "Software Engineer (Enterprise)"
        ));

        response.put("available_from", LocalDate.now().toString());
        response.put("ready_to_relocate", true);
        response.put("contact", "update-with-your-email@gmail.com");
        response.put("github", "https://github.com/YOUR_USERNAME/verichain-ai-logistics");

        response.put("coffee_dependency", "CRITICAL — but negotiable with a good offer");

        return ResponseEntity.ok(response);
    }
}
