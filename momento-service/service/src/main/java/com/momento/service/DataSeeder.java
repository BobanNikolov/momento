package com.momento.service;

import com.momento.data.model.UserAccount;
import com.momento.data.model.enums.UserRole;
import com.momento.data.model.enums.UserStatus;
import com.momento.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.admin.email:admin@momento.com}") String adminEmail,
                      @Value("${app.admin.password:admin123}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            UserAccount admin = new UserAccount();
            admin.setRole(UserRole.ADMIN);
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
            LOGGER.info("Default admin user created with email: {}", adminEmail);
        } else {
            LOGGER.info("Users already exist, skipping data seeding");
        }
    }
}
