package com.verichain.logistics.config;

import com.verichain.logistics.model.User;
import com.verichain.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with demo users on first startup.
 * Safe to run multiple times — checks before inserting.
 *
 * Demo credentials:
 *   Admin   → admin@verichain.com  / admin123
 *   Manager → manager@verichain.com / manager123
 *   Driver  → driver@verichain.com  / driver123
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("Admin User", "admin@verichain.com", "admin123", User.Role.ADMIN);
        seedUser("Warehouse Manager", "manager@verichain.com", "manager123", User.Role.WAREHOUSE_MANAGER);
        seedUser("Delivery Driver", "driver@verichain.com", "driver123", User.Role.DRIVER);
        log.info("Demo users seeded. Login at POST /api/auth/login");
    }

    private void seedUser(String name, String email, String rawPassword, User.Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .build();
            userRepository.save(user);
            log.info("Seeded user: {} [{}]", email, role);
        }
    }
}
